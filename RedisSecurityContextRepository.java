public class RedisSecurityContextRepository implements SecurityContextRepository {

  private static final Logger log = LoggerFactory.getLogger(RedisSecurityContextRepository.class);

  @Autowired
  private CacheService cacheService;

  @Override
  public boolean containsContext(HttpServletRequest request) {
    // get USER SESSION COOKIE
    String securityContextId = SecurityUtil.getSecurityContextId(request);
    log.debug("check containContext method : {}", securityContextId);
    if (StringUtils.isNotEmpty(securityContextId)) {
      return cacheService.getSecurityContext(securityContextId) != null;
    }
    return false;
  }

  @Override
  public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
    log.debug("load context...");
    HttpServletRequest request = requestResponseHolder.getRequest();
    // get USER SESSION COOKIE
    SecurityContext savedSecurityContext = null;
    String securityContextId = SecurityUtil.getSecurityContextId(request);
    if (StringUtils.isNotEmpty(securityContextId)) {
      log.debug("found security context :{}", securityContextId);
      savedSecurityContext = (SecurityContext) cacheService.getSecurityContext(securityContextId);
    }
    if (savedSecurityContext != null) {
      log.debug("return security context :{}", savedSecurityContext);
      return savedSecurityContext;
    }
    log.debug("returning empty security context");
    return SecurityContextHolder.createEmptyContext();
  }

  @Override
  public void saveContext(SecurityContext sc, HttpServletRequest request,
      HttpServletResponse response) {
    if (sc.getAuthentication() != null
        && !(sc.getAuthentication() instanceof AnonymousAuthenticationToken)
        && sc.getAuthentication().isAuthenticated()) {
      String securityContextId = (String) request.getAttribute("securityContextId");
      // String securityContextId = SecurityUtil.getSecurityContextId(request);
      log.debug("saving security context, id : {}", securityContextId);
      if (StringUtils.isNotEmpty(securityContextId)) {
        log.debug("security context : {} is not empty", securityContextId);
        SecurityContext savedSecurityContext =
            (SecurityContext) cacheService.getSecurityContext(securityContextId);
        if (savedSecurityContext == null
            || !sc.getAuthentication().getName()
                .equals(savedSecurityContext.getAuthentication().getName())) {
          log.debug("no saved security context, put session into redis.");
          cacheService.putSecurityContext(securityContextId, sc);
        }
      }
    }
  }


}
