package museium;
import redis.clients.jedis.Jedis;
import java.util.*;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class RestfulController {
	private Jedis jedis = new Jedis("localhost");
	
	@GetMapping("/testRedis")
	public String testMethod() {
		String s = "";
		Set<String> keys = jedis.keys("*");
		for (String key:keys) {
			s += key + " ";
		}
		return s;
	}
	
}
