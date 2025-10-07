package practice.postserver.global.redis.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import practice.postserver.global.redis.entity.RedisRefreshToken;

@Repository
public interface RedisRepository extends CrudRepository<RedisRefreshToken, Long> {
}
