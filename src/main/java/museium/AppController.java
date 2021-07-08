package museium;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@Controller
public class AppController {

    @RequestMapping("/create")
    public String createPage() {
        return "createlisting.html";
    }
    
    @RequestMapping("/listing")
    public String listingPage() {
        return "listings.html";
    }
}