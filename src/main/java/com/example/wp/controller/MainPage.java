package com.example.wp.controller;

import com.example.wp.domain.Point;
import com.example.wp.form.PointsCredentials;
import com.example.wp.form.UserCredentials;
import com.example.wp.form.validator.PointCredentialsValidator;
import com.example.wp.mbeans.PercentageCounterMBean;
import com.example.wp.mbeans.PointsCounterMBean;
import com.example.wp.repository.UserRepository;
import com.example.wp.service.PointService;
import com.example.wp.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@CrossOrigin
@RestController
public class MainPage {
    private final UserService userService;
    private final PointService pointService;
    private final PointCredentialsValidator pointCredentialsValidator;
    private final UserRepository userRepository;
    private final PointsCounterMBean pointsCounterMBean;
    private final PercentageCounterMBean percentageCounterMBean;

    public MainPage(UserService userService, PointService pointService, PointCredentialsValidator pointCredentialsValidator, UserRepository userRepository, PointsCounterMBean pointsCounterMBean, PercentageCounterMBean percentageCounterMBean) {
        this.userService = userService;
        this.pointService = pointService;
        this.pointCredentialsValidator = pointCredentialsValidator;
        this.userRepository = userRepository;
        this.pointsCounterMBean = pointsCounterMBean;
        this.percentageCounterMBean = percentageCounterMBean;
    }

    @GetMapping("/main")
    public String getMain() {
        System.out.println("received get request!");
        return "forward:/index.html";
    }

    @GetMapping("/point")
    public void getPoint() {

    }

    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(pointCredentialsValidator);
    }


    @PostMapping("/point")
    public ResponseEntity setPoint(@Valid @ModelAttribute("checkForm") PointsCredentials point, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            System.out.println("error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error");
        }
        point.setResult(point.getX(), point.getY(), point.getR());
        System.out.println(point.getX());
        System.out.println(point.getY());
        System.out.println(point.getR());
        try {
            pointService.register(point);
        } catch (IllegalArgumentException e) {
            Map<String, String> resp = new HashMap<>();
            resp.put("error", "Вы удалены из жизни");
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(resp);
        }
        List<Point> points = pointService.getAllPointsByUser(userRepository.getUserByLogin(point.getLogin()));
        StringJoiner joiner = new StringJoiner(",");
        for (Point point1 : points) {
            StringBuilder builder = new StringBuilder();
            builder.append("{\"x\":\"");
            builder.append(String.format("%.2f", point1.getX()));
            builder.append("\", \"y\":\"");
            builder.append(String.format("%.2f", point1.getY()));
            builder.append("\", \"r\":\"");
            builder.append(String.format("%.2f", point1.getR()));
            builder.append("\", \"result\":\"");
            builder.append(point1.getResult());
            builder.append("\"}");
            joiner.add(builder.toString());
        }
        pointsCounterMBean.updateCounters(points.get(points.size() - 1));
        percentageCounterMBean.updateCounters(pointsCounterMBean.getHitPointsCounter(), pointsCounterMBean.getAllPointsCounter());
        System.out.println(joiner.toString());
        return ResponseEntity.ok("[" + joiner.toString() + "]");
    }
}
