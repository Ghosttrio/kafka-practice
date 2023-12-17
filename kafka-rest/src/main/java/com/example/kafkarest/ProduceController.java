package com.example.kafkarest;

import com.google.gson.Gson;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ProduceController {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public ProduceController(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @GetMapping("/api/select")
    public void selectColor(@RequestHeader("user-agent") String userAgentName,
                            @RequestParam(value = "color") String colorName,
                            @RequestParam(value = "user") String userName){
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
        Date now = new Date();
        Gson gson = new Gson();

        UserEventVO userEventVO = new UserEventVO(sdfDate.format(now), userAgentName, colorName, userName);

        String json = gson.toJson(userEventVO);
        kafkaTemplate.send("select-color", json).whenComplete((result, ex) -> {
            if(ex == null) {
                log.info(result.toString());
            } else {
                ex.printStackTrace();
            }
        });

    }
}
