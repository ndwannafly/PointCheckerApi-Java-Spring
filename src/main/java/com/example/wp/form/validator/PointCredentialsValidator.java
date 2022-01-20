package com.example.wp.form.validator;
import com.example.wp.form.PointsCredentials;
import com.example.wp.form.UserCredentials;
import com.example.wp.service.PointService;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import javax.validation.ValidationException;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;


@Component
public class PointCredentialsValidator implements Validator {
    public PointCredentialsValidator(PointService pointService) {
    }

    @NotEmpty
    @NotNull
    @Override
    public boolean supports(Class<?> aClass) {
        return PointsCredentials.class.equals(aClass);
    }

    @NotEmpty
    @NotNull
    @Override
    public void validate(Object o, Errors errors) {
        if(!errors.hasErrors()){
            PointsCredentials pointsCredentials = (PointsCredentials) o;
            try {
                double x = Double.parseDouble(pointsCredentials.getX());
                double y = Double.parseDouble(pointsCredentials.getY());
                double r = Double.parseDouble(pointsCredentials.getR());
                if(x > 4 || x < -4) throw new ValidationException("x is out of range!");
                if(y < -2 || y > 2) throw new ValidationException("y is out of range!");
                if(!(r == 1 || r == 1.5 || r == 2 || r == 2.5 || r == 3)){
                    throw new ValidationException("r is out of range!");
                }
            }catch (Exception e){
                e.printStackTrace();
                errors.rejectValue("point", "point.error", "invalid values in point");
            }
        }
    }
}
