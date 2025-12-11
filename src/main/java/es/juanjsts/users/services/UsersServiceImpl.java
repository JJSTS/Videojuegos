package es.juanjsts.users.services;

import es.juanjsts.users.dto.UserInfoResponse;
import es.juanjsts.users.dto.UserRequest;
import es.juanjsts.users.dto.UserResponse;
import es.juanjsts.users.exceptions.UserNameOrEmailExists;
import es.juanjsts.users.exceptions.UserNotFound;
import es.juanjsts.users.mappers.UsersMapper;
import es.juanjsts.users.models.User;
import es.juanjsts.users.repositories.UsersRepository;
import es.juanjsts.videojuegos.repositories.VideojuegosRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = {"users"})
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;
    private final VideojuegosRepository videojuegosRepository;

    @Override
    public Page<UserResponse> findAll(Optional<String> username, Optional<String> email, Optional<Boolean> isDeleted, Pageable pageable) {
        log.info("Buscando todos los usuarios con username: {} y borrados: {}", username, isDeleted);
        Specification<User> specUsernameUser = (root, criteriaQuery, criteriaBuilder) ->
                username.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("username")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> specEmailUser = (root, criteriaQuery, criteriaBuilder) ->
                email.map(m -> criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), "%" + m.toLowerCase() + "%"))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> specIsDeleted = (root, criteriaQuery, criteriaBuilder) ->
                isDeleted.map(m -> criteriaBuilder.equal(root.get("isDeleted"), m))
                        .orElseGet(() -> criteriaBuilder.isTrue(criteriaBuilder.literal(true)));

        Specification<User> criterio = Specification.allOf(
                specUsernameUser,
                specEmailUser,
                specIsDeleted
        );
        return usersRepository.findAll(criterio, pageable).map(usersMapper::toUserResponse);
    }

    @Override
    @Cacheable(key = "#id")
    public UserInfoResponse findById(Long id) {
        log.info("Buscando el usuario con id: {}", id);
        var user = usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        var videojuegos = videojuegosRepository.findByUsuarioId(id).stream().map(p -> p.getNombre()).toList();
        return usersMapper.toUserInfoResponse(user, videojuegos);
    }

    @Override
    @CachePut(key = "#result.id")
    public UserResponse save(UserRequest userRequest) {
        log.info("Guardando usuario: {}", userRequest);
        usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u ->{
                    throw new UserNameOrEmailExists("Ya existe un usuario con ese username o email");
                });
        return usersMapper.toUserResponse(usersRepository.save(usersMapper.toUser(userRequest)));
    }

    @Override
    @CachePut(key = "#result.id")
    public UserResponse update(Long id, UserRequest userRequest) {
        log.info("Actualizando usuario: {}", userRequest);
        usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        usersRepository.findByUsernameEqualsIgnoreCaseOrEmailEqualsIgnoreCase(userRequest.getUsername(), userRequest.getEmail())
                .ifPresent(u -> {
                    if (!u.getId().equals(id)) {
                        System.out.println("Usuario encontrado: " + u.getId() + " Mi id: " + id);
                        throw new UserNameOrEmailExists("Ya existe un usuario con ese username o email");
                    }
                });
        return usersMapper.toUserResponse(usersRepository.save(usersMapper.toUser(userRequest, id)));
    }

    @Override
    @Transactional
    @CacheEvict(key = "#id")
    public void deleteById(Long id) {
        log.info("Borrando usuario por id: {}", id);
        User user = usersRepository.findById(id).orElseThrow(() -> new UserNotFound(id));
        if (videojuegosRepository.existsByUsuarioId(id)) {
            log.info("Borrando lógico de usuario por id: {}", id);
            usersRepository.updateIsDeletedToTrueById(id);
        } else  {
            log.info("Borrando físico usuario por id: {}", id);
            usersRepository.delete(user);
        }
    }

    @Override
    public List<User> findAllActiveUsers() {
        log.info("Buscando todos los usuarios");
        return usersRepository.findAllByIsDeletedFalse();
    }
}
