package drishti.payment.calculator.factory;


import drishti.payment.calculator.config.ApplicationContextProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SummonFactory {


    public <T> T getChannelById(String channelId) {
        try {
            Class<?> klass = Class.forName("digit.util.drool.DroolHelper");

            ApplicationContext context = ApplicationContextProvider.getApplicationContext();

            return (T) context.getBean(klass);

        } catch (Exception e) {
            log.error("Error occurred while fetching object for class" + e.getMessage());
        }
        return null;
    }
}
