package org.example.secureplatform;

import org.example.secureplatform.common.JwtAuthenticationTokenFilter;
import org.example.secureplatform.common.RedisCache;
import org.example.secureplatform.common.SystemInfoUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SecurePlatformApplicationTests {

	@Autowired
	private RedisCache redisCache;

	@Autowired
	private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

	private SystemInfoUtil systemInfoUtil;

	@Test
	void contextLoads() {
//		String jwt = JwtUtil.createJWT("12", "whoami", 60*60*1000L);
//		System.out.println(jwt);

		/*  JWT
		try {
			String userid;
			String token = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiI1NDYxMmQxZjk4OTQ0MTA3YTlkZmUzYTdmOWIwMWViYiIsInN1YiI6IjEiLCJpc3MiOiJzZyIsImlhdCI6MTczMzU4MDY0OCwiZXhwIjoxNzMzNTg0MjQ4fQ.c41m6sSdGe0CF6N3o5n7xXm3rlNOjd825mZiq3v4zOk";
			try {
				Claims claims = JwtUtil.parseJWT(token);
				userid = claims.getSubject();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("token非法");
			}
			String redisKey = "user" + userid;
			System.out.println("redisKey = " + redisKey);
			LoginUser loginUser = redisCache.getCacheObject(redisKey);
			System.out.println("loginUser = " + loginUser);
		}catch (Exception e){
			throw new RuntimeException(e);
		}
		*/
	}
//	@Autowired
//	private UserMapper userMapper;
//
//	@Test
//	public void testUserMapper(){
//		List<User> users = userMapper.selectList(null);
//		System.out.println(users);
//	}
}
