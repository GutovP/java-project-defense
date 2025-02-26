package flower_shop.web;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static flower_shop.web.Paths.API_V1_BASE_PATH;

@RestController
@RequestMapping(API_V1_BASE_PATH + "/auth")
public class UserController {
}
