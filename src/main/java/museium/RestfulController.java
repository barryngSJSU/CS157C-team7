package museium;
import redis.clients.jedis.Jedis;
import java.util.*;
import org.json.JSONObject;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
	
	@PostMapping("/listing/create")
	public Map<String, String> createListing(@RequestParam("title") String title, @RequestParam("description") String description, @RequestParam("price") String price, @RequestParam("image") String image) {
		Map<String, String> hash = new HashMap<String, String>();
		hash.put("Description", description);
		hash.put("Price", price);
		hash.put("Image", image);
		jedis.hset(title, "Description", description);
		jedis.hset(title, "Price", price);
		jedis.hset(title, "Image", image);
		jedis.rpush("listings", title);
		jedis.save();
		return hash;
	}
	
	@GetMapping("/listing/list")
	public List<String> listListings() {
		List<String> listings = jedis.lrange("listings", 0, -1);
		return listings;
	}
	
	@GetMapping("/listing/{title}")
	public Map<String, String> getListing(@PathVariable String title) {
		Map<String, String> hash = jedis.hgetAll(title);
		return hash;
	}
	
}
