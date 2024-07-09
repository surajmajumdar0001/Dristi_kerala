package digit.validator;

import digit.web.models.JudgeCalendarRule;
import digit.web.models.enums.JudgeRuleType;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;


@ExtendWith(MockitoExtension.class)
public class JudgeCalendarValidatorTest {


    @InjectMocks
    private JudgeCalendarValidator validator;

    @Test
    public void testValidateUpdateJudgeCalendar() {
        List<JudgeCalendarRule> judgeCalendarRule = new ArrayList<>();
        JudgeCalendarRule rule = new JudgeCalendarRule();
        rule.setTenantId("tenantId");
        rule.setJudgeId("judgeId");
        rule.setDate(LocalDate.now());
        rule.setRuleType(JudgeRuleType.OTHER);
        judgeCalendarRule.add(rule);
        validator.validateUpdateJudgeCalendar(judgeCalendarRule);
    }

    @Test
    public void testValidateUpdateJudgeCalendar_WithEmptyTenantId() {
        List<JudgeCalendarRule> judgeCalendarRule = new ArrayList<>();
        JudgeCalendarRule rule = new JudgeCalendarRule();
        rule.setJudgeId("judgeId");
        rule.setDate(LocalDate.now());
        rule.setRuleType(JudgeRuleType.OTHER);
        judgeCalendarRule.add(rule);
        assertThrows(CustomException.class, () -> validator.validateUpdateJudgeCalendar(judgeCalendarRule));
    }

    @Test
    public void testValidateUpdateJudgeCalendar_WithEmptyJudgeId() {
        List<JudgeCalendarRule> judgeCalendarRule = new ArrayList<>();
        JudgeCalendarRule rule = new JudgeCalendarRule();
        rule.setTenantId("tenantId");
        rule.setDate(LocalDate.now());
        rule.setRuleType(JudgeRuleType.OTHER);
        judgeCalendarRule.add(rule);
        assertThrows(CustomException.class, () -> validator.validateUpdateJudgeCalendar(judgeCalendarRule));
    }

    @Test
    public void testValidateUpdateJudgeCalendar_WithEmptyDate() {
        List<JudgeCalendarRule> judgeCalendarRule = new ArrayList<>();
        JudgeCalendarRule rule = new JudgeCalendarRule();
        rule.setTenantId("tenantId");
        rule.setJudgeId("judgeId");
        rule.setRuleType(JudgeRuleType.OTHER);
        judgeCalendarRule.add(rule);

        assertThrows(CustomException.class, () -> validator.validateUpdateJudgeCalendar(judgeCalendarRule));
    }

    @Test
    public void testValidateUpdateJudgeCalendar_WithPastDate() {
        List<JudgeCalendarRule> judgeCalendarRule = new ArrayList<>();
        JudgeCalendarRule rule = new JudgeCalendarRule();
        rule.setTenantId("tenantId");
        rule.setJudgeId("judgeId");
        rule.setDate(LocalDate.now().minusDays(1));
        rule.setRuleType(JudgeRuleType.OTHER);
        judgeCalendarRule.add(rule);
        assertThrows(CustomException.class, () -> validator.validateUpdateJudgeCalendar(judgeCalendarRule));
    }

    @Test
    public void testValidateUpdateJudgeCalendar_WithEmptyRuleType() {
        List<JudgeCalendarRule> judgeCalendarRule = new ArrayList<>();
        JudgeCalendarRule rule = new JudgeCalendarRule();
        rule.setTenantId("tenantId");
        rule.setJudgeId("judgeId");
        rule.setDate(LocalDate.now());
        judgeCalendarRule.add(rule);
        assertThrows(CustomException.class, () -> validator.validateUpdateJudgeCalendar(judgeCalendarRule));
    }

    @Test
    public void testValidateUpdateJudgeCalendar_WithEmptyList() {
        List<JudgeCalendarRule> judgeCalendarRule = new ArrayList<>();
        validator.validateUpdateJudgeCalendar(judgeCalendarRule);
    }
}

