public class CacheServiceImpl {


    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void putSecurityContext(String userSessionId, Object value) {
        redisTemplate.opsForHash().put(SECURITY_CONTEXT, userSessionId, value);
    }

    @Override
    public Object getSecurityContext(String userSessionId) {
        return redisTemplate.opsForHash().get(SECURITY_CONTEXT, userSessionId);
    }

    @Override
    public void deleteSecurityContext(String userSessionId) {
        redisTemplate.opsForHash().delete(SECURITY_CONTEXT, userSessionId);
    }


}
