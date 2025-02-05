package config.controller;

/**
 * Created by Chenjie Xu on 2017/5/11.
 */

import config.entity.Config;
import config.service.ConfigService;
import edu.fudan.common.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("api/v1/configservice")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @GetMapping(path = "/welcome")
    public String home(@RequestHeader HttpHeaders headers) {

        return "Welcome to [ Config Service ] !";
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/configs")
    public HttpEntity queryAll(@RequestHeader HttpHeaders headers) {

        return ok(configService.queryAll(headers));
    }

    @CrossOrigin(origins = "*")
    @PostMapping(value = "/configs")
    public HttpEntity<?> createConfig(@RequestBody Config info, @RequestHeader HttpHeaders headers) {
        return new ResponseEntity<>(configService.create(info, headers), HttpStatus.CREATED);
    }

    @CrossOrigin(origins = "*")
    @PutMapping(value = "/configs")
    public HttpEntity updateConfig(@RequestBody Config info, @RequestHeader HttpHeaders headers) {
        return ok(configService.update(info, headers));
    }


    @CrossOrigin(origins = "*")
    @DeleteMapping(value = "/configs/{configName}")
    public HttpEntity deleteConfig(@PathVariable String configName, @RequestHeader HttpHeaders headers) {
        return ok(configService.delete(configName, headers));
    }

    @CrossOrigin(origins = "*")
    @GetMapping(value = "/configs/{configName}")
    public HttpEntity retrieve(@PathVariable String configName, @RequestHeader HttpHeaders headers) {
        return ok(configService.query(configName, headers));
    }

//    @CrossOrigin(origins = "*")
//    @RequestMapping(value="/config/query", method = RequestMethod.POST)
//    public String query(@PathVariable String name, @RequestHeader HttpHeaders headers){
//        return configService.query(info, headers);
//    }


}
