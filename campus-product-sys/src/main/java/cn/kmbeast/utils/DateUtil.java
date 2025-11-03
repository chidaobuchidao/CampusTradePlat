package cn.kmbeast.utils;

import cn.kmbeast.pojo.dto.query.base.QueryDto;
import cn.kmbeast.pojo.vo.ChartVO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 时间工具类
 */
public class DateUtil {

    /**
     * 构造时间查询器，即指定的开始时间、结束时间
     *
     * @param days 时间范围
     * @return PagerDTO
     */
    public static QueryDto startAndEndTime(Integer days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextDayStart = now.minusDays(days).plusDays(1).with(LocalTime.of(0, 0)); // 下一天的开始时间
        LocalDateTime daysAgoEnd = nextDayStart.minusSeconds(1);
        return QueryDto.builder().startTime(daysAgoEnd).endTime(now).build();
    }

    /**
     * 统计指定天数内的记录数
     *
     * @param dayRange 往前查多少天
     * @param dates    数据源
     * @return Map<String, Integer>
     */
    public static List<ChartVO> countDatesWithinRange(Integer dayRange, List<LocalDateTime> dates) {
        // 将所有日期转换为 LocalDate，并统计每个日期出现的次数
        Map<LocalDate, Long> dateCounts = dates.stream()
                .map(LocalDateTime::toLocalDate)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        LocalDate startDate = LocalDate.now().minusDays(dayRange);
        return IntStream.rangeClosed(0, dayRange)
                .mapToObj(startDate::plusDays)
                .filter(dateCounts::containsKey)  // 只包含有记录的日期
                .map(date -> new ChartVO(
                        String.format("%02d-%02d", date.getMonthValue(), date.getDayOfMonth()),
                        dateCounts.getOrDefault(date, 0L).intValue()))
                .collect(Collectors.toList());
    }
}
