package template.feature.auth;

import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Value
class Token {

  String value;
  // expiration

  static Token from(String token) {
    return new Token(token);
  }

//  private enum Props {
//    ACTIVE, ROLE, ID, NAME, EMAIL, PASSWORD;
//
//    final String fromClaim(final Claims claims) {
//      return (String) claims.get(name());
//    }
//  }
//  static final String BEARER_PREFIX = "Bearer ";
//
//  private static final Pattern BEARER_REGEX = Pattern.compile(BEARER_PREFIX);
//  private static final Pattern COLLECTION_REGEX = Pattern.compile("[\\[\\]]");
//  private static final Duration TOKEN_EXPIRATION = Duration.ofHours(1);
//
//  Claims claims;
//
//  static Token from(final @NonNull String secret, final @NonNull String token) {
//    log.trace("Token input: {}", token);
//    val value = Arrays.stream(BEARER_REGEX.split(token))
//                      .filter(Predicate.not(String::isBlank))
//                      .findAny().orElseThrow(IllegalArgumentException::new);
//    return new Token(Jwts.parser()
//                         .setSigningKey(secret)
//                         .parseClaimsJws(value).getBody());
//  }
//
//  static Token fromUser(final @NonNull User user) {
//    val claims = Jwts.claims()
//                     .setSubject(user.getId())
//                     // .setAudience("")
//                     // .setIssuer("")
//                     .setExpiration(Date.from(
//                         Instant.now().plus(TOKEN_EXPIRATION)))
//                     .setIssuedAt(Date.from(Instant.now()))
//                     .setNotBefore(Date.from(Instant.now()));
//
//    claims.put(Props.ACTIVE.name(), user.isActive());
//    claims.put(Props.ROLE.name(), user.getRoles());
//    claims.put(Props.ID.name(), user.getId());
//    claims.put(Props.NAME.name(), user.getName());
//    claims.put(Props.EMAIL.name(), user.getEmail());
//    claims.put(Props.PASSWORD.name(), user.getPassword());
//
//    return new Token(claims);
//  }
//
//  final User toUser() {
//    return User.of(
//        Boolean.parseBoolean(Props.ACTIVE.fromClaim(claims)),
//        Arrays
//            .stream(COLLECTION_REGEX.split(Props.ROLE.fromClaim(claims)))
//            .map(User.Role::valueOf)
//            .collect(Collectors.toSet()),
//        Props.ID.fromClaim(claims),
//        Props.NAME.fromClaim(claims),
//        Props.EMAIL.fromClaim(claims),
//        Props.PASSWORD.fromClaim(claims));
//  }
//
//  final String generate(final Key secret) {
//    return Jwts.builder().setClaims(claims)
//               .signWith(secret, SignatureAlgorithm.HS512).compact();
//  }
//
//  final boolean isExpired() {
//    return claims.getExpiration().toInstant().isBefore(Instant.now());
//  }
//
//  final Token refresh() {
//    return new Token
//        (claims.setExpiration(Date.from(Instant.now().plus(TOKEN_EXPIRATION))));
//  }
}
