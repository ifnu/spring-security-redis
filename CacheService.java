public interface CacheService {
  // method to handle security context storage in spring security for responsive design
  void putSecurityContext(String userSessionId, Object value);

  Object getSecurityContext(String userSessionId);

  void deleteSecurityContext(String userSessionId);
}
