package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WorkspaceService workspaceService;

    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    @Transactional
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //创建日期列表
        List<LocalDate> dateList = createDateList(begin, end);

        //转换为字符串
        String dateListStr = StringUtils.join(dateList, ",");

        //创建营业额列表
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //查询营业额
            Map<String, Object> turnoverMap = new HashMap<>();
            turnoverMap.put("beginTime", beginTime);
            turnoverMap.put("endTime", endTime);
            turnoverMap.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumAmountByMap(turnoverMap);
            //处理空值
            if (turnover == null) turnover = 0.0;
            turnoverList.add(turnover);
        }

        //创建营业额字符串
        String turnoverListStr = StringUtils.join(turnoverList, ",");

        return TurnoverReportVO.builder()
                .dateList(dateListStr)
                .turnoverList(turnoverListStr)
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    @Transactional
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //创建日期列表
        List<LocalDate> dateList = createDateList(begin, end);

        //转换为字符串
        String dateListStr = StringUtils.join(dateList, ",");

        List<Integer> totalUserList = new ArrayList<>();
        List<Integer> newUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //查询总用户数
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("endTime", endTime);
            totalUserList.add(userMapper.countByMap(userMap));

            //查询新增用户数
            userMap.put("beginTime", beginTime);
            newUserList.add(userMapper.countByMap(userMap));

        }

        //创建用户总量字符串
        String totalUserListStr = StringUtils.join(totalUserList, ",");
        //创建新增用户字符串
        String newUserListStr = StringUtils.join(newUserList, ",");

        return UserReportVO.builder()
                .dateList(dateListStr)
                .totalUserList(totalUserListStr)
                .newUserList(newUserListStr)
                .build();

    }

    /**
     * 销量Top10统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    @Transactional
    public SalesTop10ReportVO getTop10SalesStatistics(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        //查询Top10销售商品
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getTop10ByBeginAndEnd(beginTime, endTime);

        //创建Top10销售名字列表
        List<String> nameList = goodsSalesDTOList
                .stream()
                .map(GoodsSalesDTO::getName)
                .collect(Collectors.toList());
        //创建Top10销售数量列表
        List<Integer> numberList = goodsSalesDTOList
                .stream()
                .map(GoodsSalesDTO::getNumber)
                .collect(Collectors.toList());



        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList, ","))
                .numberList(StringUtils.join(numberList, ","))
                .build();
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    @Transactional
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //创建日期列表
        List<LocalDate> dateList = createDateList(begin, end);

        //创建订单数列表
        List<Integer> orderContList = new ArrayList<>();
        //创建有效订单数列表
        List<Integer> validOrderCountContList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);

            //查询订单数
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("beginTime", beginTime);
            orderMap.put("endTime", endTime);
            orderContList.add(orderMapper.countByMap(orderMap));

            //查询有效订单数
            orderMap.put("status", Orders.COMPLETED);
            validOrderCountContList.add(orderMapper.countByMap(orderMap));
        }

        //计算总订单数
        Integer totalOrderCount = orderContList
                .stream()
                .reduce(Integer::sum)
                .get();
        //计算有效订单数
        Integer validOrderCount = validOrderCountContList
                .stream()
                .reduce(Integer::sum)
                .get();
        //计算订单完成率
        Double orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount.doubleValue();

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderContList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountContList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 导出Excel
     * @param response
         */
    @Override
    public void exportExcel(HttpServletResponse response) {
        //设置最近30日时间
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDate end = LocalDate.now().minusDays(1);

        //查询营业数据
        BusinessDataVO businessDataVO = workspaceService.getBusinessData
                (LocalDateTime.of(begin, LocalTime.MIN),
                LocalDateTime.of(end, LocalTime.MAX));

        //加载数据报表模板
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");

        //创建Excel文件
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);

            //获取工作表
            XSSFSheet sheet = excel.getSheet("sheet1");

            //设置时间范围
            sheet.getRow(1)
                    .getCell(1)
                    .setCellValue("从" + begin + "至" + end);

            //设置概览数据
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessDataVO.getTurnover());
            row.getCell(4).setCellValue(businessDataVO.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessDataVO.getNewUsers());

            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessDataVO.getValidOrderCount());
            row.getCell(4).setCellValue(businessDataVO.getUnitPrice());

            //设置明细数据
            for (int i = 0; i < 30; i++) {
                //设置日期
                LocalDate date = LocalDate.now().minusDays(i);

                //获取该日期下的订单数据
                BusinessDataVO data = workspaceService.getBusinessData(
                        LocalDateTime.of(date, LocalTime.MIN),
                        LocalDateTime.of(date, LocalTime.MAX)
                );

                row = sheet.getRow(i+7);
                row.getCell(1).setCellValue(date.toString());
                row.getCell(2).setCellValue(data.getTurnover());
                row.getCell(3).setCellValue(data.getValidOrderCount());
                row.getCell(4).setCellValue(data.getOrderCompletionRate());
                row.getCell(5).setCellValue(data.getUnitPrice());
                row.getCell(6).setCellValue(data.getNewUsers());
            }

            //将数据下载到客户端
            excel.write(response.getOutputStream());
            excel.close();
            in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private List<LocalDate> createDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();

        dateList.add(begin);
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        return dateList;
    }
}
