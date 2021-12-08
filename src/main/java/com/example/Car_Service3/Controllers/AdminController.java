package com.example.Car_Service3.Controllers;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.*;
import com.example.Car_Service3.Entity.*;
import com.example.Car_Service3.Repo.*;
import com.example.Car_Service3.Service.MailSender;
import com.example.Car_Service3.Service.ZipUtility;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jms.JmsAutoConfiguration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.opencsv.bean.CsvToBeanBuilder;

import javax.mail.MessagingException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.zip.ZipOutputStream;

@Controller
public class AdminController {

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    ManagersRepository managersRepository;
    @Autowired
    IssueRepository issueRepository;
    @Autowired
    ServiceRepository serviceRepository;
    @Autowired
    InsuranceRepository insuranceRepository;
    @Autowired
    RentalRepository rentalRepository;
    @Autowired
    VacanciesRepository vacanciesRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TestDetailsRepository testDetailsRepository;
    @Autowired
    private MailSender mailSender;
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String getAdminPage(Model model)
    {
        model.addAttribute(new Managers());
        return "admin";
    }
    @RequestMapping(value = "/testing", method = RequestMethod.GET)
    public String getTestingPage(Model model)
    {
        model.addAttribute(new TestDetails());
        return "startTest";
    }
    @RequestMapping(value = "/testResults", method = RequestMethod.GET)
    public String getResultsPage(Model model)
    {
        Iterable<TestDetails> testDetails = testDetailsRepository.findAll();
        Iterator<TestDetails> iterator = testDetails.iterator();
        ArrayList<TestDetails> result = new ArrayList<>();
        iterator.forEachRemaining(result::add);
        model.addAttribute(result);
        return "testResults";
    }
    @RequestMapping(value = "/log", method =  RequestMethod.GET)
    public String getLogInfo(@RequestParam(name = "id")int testID, Model model)
    {
        TestDetails test = testDetailsRepository.findById(testID);
        List<String> log = new ArrayList<>();
        try{
            FileInputStream fstream = new FileInputStream("C:\\jmeter\\apache-jmeter-5.4.1\\bin\\tests\\jmeter_"+ test.getExecutionTime() + ".log");
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
            String strLine;
            while ((strLine = br.readLine()) != null)   {
                System.out.println (strLine);
                log.add(strLine);
            }
            fstream.close();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        model.addAttribute(log);
        return "logs";
    }
    @RequestMapping(value = "/proceedTest", method = RequestMethod.POST)
    public String startTest(@ModelAttribute TestDetails testDetails, Model model) throws IOException
    {
        java.util.Date utilDate = new java.util.Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        testDetails.setExecutionTime(sqlDate);
        Process process = Runtime.getRuntime().exec("C:\\jmeter\\apache-jmeter-5.4.1\\bin\\jmeter.bat -n -t" +
                " C:\\jmeter\\apache-jmeter-5.4.1\\bin\\tests\\" + testDetails.getTestName() + ".jmx -JENV_URL="+ testDetails.getTestUrl() + " -JVUSERS=" + testDetails.getUsers() +
                " -JRAMP_UP=" + testDetails.getRampUp() + " -JDURATION=" + testDetails.getDuration() + " -JTHINK_TIME=" + testDetails.getThinkTime() +
                " -JTHINK_TIME_DEVIATION=" + testDetails.getThinkTimeDeviation() + " -j C:\\jmeter\\apache-jmeter-5.4.1\\bin\\tests\\jmeter_" + testDetails.getExecutionTime() + ".log -l C:\\jmeter\\apache-jmeter-5.4.1\\bin\\tests\\jmeter_execution_" + testDetails.getExecutionTime() + ".jtl -e -o C:\\jmeter\\apache-jmeter-5.4.1\\bin\\tests\\HTMLReport_"+ testDetails.getExecutionTime());
        testDetailsRepository.save(testDetails);
        //printResults(process);
        return getResultsPage(model);
    }
    public static void printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
    @RequestMapping(value = "/downloadHTML", method = RequestMethod.GET, produces="application/zip")
    public void getHTMLReportPage(@RequestParam(name = "id")int testID, HttpServletResponse response) throws IOException, MessagingException
    {
        TestDetails test = testDetailsRepository.findById(testID);
        sendMessage(test, "Результаты тестирования " + test.getTestName());
        File file = new File("C:\\jmeter\\apache-jmeter-5.4.1\\bin\\tests\\HTMLReport_" + test.getExecutionTime());
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/zip");
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"HTML_Report.zip\"");
        ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream());
        ZipUtility.zipFile(file, file.getName(), zipOut);
        zipOut.close();
    }
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String searchForName(@ModelAttribute Customer customer, Model model)
    {
        searchForCustomer(customer, model);
        model.addAttribute(new Managers());
        return "admin";
    }
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String saveNewManager(@ModelAttribute Managers managers, Model model)
    {
        managers.setActive((byte) 1);
        managers.setPassword(passwordEncoder.encode(managers.getPassword()));
        managersRepository.save(managers);
        managers.insertRole();
        return getAdminPage(model);
    }
    @RequestMapping(value = "/searchService", method = RequestMethod.POST)
    public String search(@ModelAttribute Customer customer, Model model)
    {

        ArrayList<Customer> customerList = new ArrayList<>(searchForService(customer, "услуга"));
        ArrayList<Service> serviceList = new ArrayList<>();
        ArrayList<Issue> issuesList = new ArrayList<>();
        model.addAttribute(customerList);
        for(int i=0;i<customerList.size();i++)
        {
            serviceList.add(serviceRepository.findByCustomer(customerList.get(i)));
            issuesList.add(issueRepository.findIssueByService(serviceList.get(i)));
        }
        model.addAttribute(serviceList);
        model.addAttribute(issuesList);
        model.addAttribute(new Customer());
        model.addAttribute(new Issue());
        return "startTest";
    }
    @RequestMapping(value = "/searchInsurance", method = RequestMethod.POST)
    public String searchInsurance(@ModelAttribute Customer customer, Model model)
    {
        ArrayList<Customer> customerList = new ArrayList<>(searchForService(customer, "страхование"));
        ArrayList<Service> serviceList = new ArrayList<>();
        ArrayList<Insurance> insuranceList = new ArrayList<>();
        model.addAttribute(customerList);
        for(int i=0;i<customerList.size();i++)
        {
            serviceList.add(serviceRepository.findByCustomer(customerList.get(i)));
            insuranceList.add(insuranceRepository.findInsuranceByService(serviceList.get(i)));
        }
        model.addAttribute(serviceList);
        model.addAttribute(insuranceList);
        model.addAttribute(new Customer());
        model.addAttribute(new Insurance());
        return "testResults";
    }
    @RequestMapping(value = "/addRental", method = RequestMethod.POST)
    public String addRental(@ModelAttribute Customer customer, Rental rental, Model model)
    {
        customer.setType("бронирование");
        customerRepository.save(customer);
        Service service = new Service();
        service.setService(UUID.randomUUID().toString());
        service.setStatus("open");
        service.setCustomer(customer);
        serviceRepository.save(service);
        rental.setService(service);
        rentalRepository.save(rental);
        return getRentalPage(model);
    }
    @RequestMapping(value = "/addVacancy", method = RequestMethod.POST)
    public String addVacancy(@ModelAttribute Customer customer, Vacancies vacancies, Model model)
    {
        customer.setType("вакансия");
        customerRepository.save(customer);
        Service service = new Service();
        service.setService(UUID.randomUUID().toString());
        service.setStatus("open");
        service.setCustomer(customer);
        serviceRepository.save(service);
        vacancies.setService(service);
        vacanciesRepository.save(vacancies);
        return getVacanciesPage(model);
    }
    @RequestMapping(value = "/searchRental", method = RequestMethod.POST)
    public String searchRental(@ModelAttribute Customer customer, Model model)
    {
        ArrayList<Customer> customerList = new ArrayList<>(searchForService(customer, "бронирование"));
        ArrayList<Service> serviceList = new ArrayList<>();
        ArrayList<Rental> rentalList = new ArrayList<>();
        model.addAttribute(customerList);
        for(int i=0;i<customerList.size();i++)
        {
            serviceList.add(serviceRepository.findByCustomer(customerList.get(i)));
            rentalList.add(rentalRepository.findRentalByService(serviceList.get(i)));
        }
        model.addAttribute(serviceList);
        model.addAttribute(rentalList);
        model.addAttribute(new Customer());
        model.addAttribute(new Rental());
        return "adminRental";
    }
    @RequestMapping(value = "/searchVacancies", method = RequestMethod.POST)
    public String searchVacancy(@ModelAttribute Customer customer, Model model)
    {
        ArrayList<Customer> customerList = new ArrayList<>(searchForService(customer, "вакансия"));
        ArrayList<Service> serviceList = new ArrayList<>();
        ArrayList<Vacancies> vacanciesList = new ArrayList<>();
        model.addAttribute(customerList);
        for(int i=0;i<customerList.size();i++)
        {
            serviceList.add(serviceRepository.findByCustomer(customerList.get(i)));
            vacanciesList.add(vacanciesRepository.findVacanciesByService(serviceList.get(i)));
        }
        model.addAttribute(serviceList);
        model.addAttribute(vacanciesList);
        model.addAttribute(new Customer());
        model.addAttribute(new Vacancies());
        return "adminVacancies";
    }
    @RequestMapping(value = "/beginRental", method = RequestMethod.GET)
    public String beginRental(@RequestParam(name = "service")String serviceId, Model model)
    {
        Service service = serviceRepository.findByService(serviceId);
        Rental rental = rentalRepository.findRentalByService(service);
        rentalRepository.delete(rental);
        service.setStatus("in progress");
        rentalRepository.save(rental);
        return getRentalPage(model);
    }
    @RequestMapping(value = "/finishRental", method = RequestMethod.GET)
    public String finishRental(@RequestParam(name = "service")String serviceId, Model model)
    {
        Service service = serviceRepository.findByService(serviceId);
        Rental rental = rentalRepository.findRentalByService(service);
        rentalRepository.delete(rental);
        service.setStatus("finish");
        rentalRepository.save(rental);
        return getRentalPage(model);
    }
    @RequestMapping(value = "/beginVacancies", method = RequestMethod.GET)
    public String beginVacancy(@RequestParam(name = "service")String serviceId, Model model)
    {
        Service service = serviceRepository.findByService(serviceId);
        Vacancies vacancies = vacanciesRepository.findVacanciesByService(service);
        vacanciesRepository.delete(vacancies);
        service.setStatus("in progress");
        vacanciesRepository.save(vacancies);
        return getVacanciesPage(model);
    }
    @RequestMapping(value = "/finishVacancies", method = RequestMethod.GET)
    public String finishVacancy(@RequestParam(name = "service")String serviceId, Model model)
    {
        Service service = serviceRepository.findByService(serviceId);
        Vacancies vacancies = vacanciesRepository.findVacanciesByService(service);
        vacanciesRepository.delete(vacancies);
        service.setStatus("finish");
        vacanciesRepository.save(vacancies);
        return getVacanciesPage(model);
    }
    @RequestMapping(value = "/adminRental", method = RequestMethod.GET)
    public String getRentalPage(Model model)
    {
        ArrayList<Customer> customerList = new ArrayList<>(customerRepository.findCustomersByType("бронирование"));
        ArrayList<Service> serviceList = new ArrayList<>();
        ArrayList<Rental> rentalList = new ArrayList<>();
        model.addAttribute(customerList);
        for(int i=0;i<customerList.size();i++)
        {
            serviceList.add(serviceRepository.findByCustomer(customerList.get(i)));
            rentalList.add(rentalRepository.findRentalByService(serviceList.get(i)));
        }
        model.addAttribute(serviceList);
        model.addAttribute(rentalList);
        model.addAttribute(new Customer());
        model.addAttribute(new Rental());
        return "adminRental";
    }
    @RequestMapping(value = "/adminVacancies", method = RequestMethod.GET)
    public String getVacanciesPage(Model model)
    {
        ArrayList<Customer> customerList = new ArrayList<>(customerRepository.findCustomersByType("вакансия"));
        ArrayList<Service> serviceList = new ArrayList<>();
        ArrayList<Vacancies> vacanciesList = new ArrayList<>();
        model.addAttribute(customerList);
        for(int i=0;i<customerList.size();i++)
        {
            serviceList.add(serviceRepository.findByCustomer(customerList.get(i)));
            vacanciesList.add(vacanciesRepository.findVacanciesByService(serviceList.get(i)));
        }
        model.addAttribute(serviceList);
        model.addAttribute(vacanciesList);
        model.addAttribute(new Customer());
        model.addAttribute(new Vacancies());
        return "adminVacancies";
    }

    public void searchForCustomer(Customer customer, Model model)
    {
        if(!customer.getName().isEmpty()) {
            ArrayList<Customer> customers = new ArrayList<>(customerRepository.findCustomersByName(customer.getName()));
            if (customers.size() > 0) {
                model.addAttribute(customers);
            }
            else
            {
                Iterable<Customer> customers1 = customerRepository.findAll();
                model.addAttribute(customers1);
            }
        }
        else {
            Iterable<Customer> customers = customerRepository.findAll();
            model.addAttribute(customers);
        }
    }

    public ArrayList<Customer> searchForService(Customer customer, String type)
    {
        if(!customer.getName().isEmpty()) {
            ArrayList<Customer> customers = new ArrayList<>(customerRepository.findCustomersByName(customer.getName()));
            if (customers.size() > 0) {
                return customers;
            }
            else
            {
                ArrayList<Customer> customerList = new ArrayList<>(customerRepository.findCustomersByType(type));
                return customerList;
            }
        }
        else {
            ArrayList<Customer> customerList = new ArrayList<>(customerRepository.findCustomersByType(type));
            return customerList;
        }
    }
    public void sendMessage(TestDetails test, String subject) throws IOException, MessagingException
    {
        String fileName = "C:\\jmeter\\apache-jmeter-5.4.1\\bin\\tests\\jmeter_execution_" + test.getExecutionTime() + ".jtl";

        List<JMeter_Results> resultsList = new CsvToBeanBuilder(new FileReader(fileName))
                .withType(JMeter_Results.class)
                .build()
                .parse();

        resultsList.removeIf(result -> result.getLabel().contains("GET") || result.getLabel().contains("POST"));

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Results");

        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        List<Integer> duration = new ArrayList<>();
        int i = 1;
        boolean res = false;
        for (JMeter_Results jMeterResult : resultsList)
        {
            if(i==16)
            {
                break;
            }
            if(jMeterResult.getLabel().equals("label"))
            {
                HSSFRow rowhead = sheet.createRow((short)0);
                rowhead.createCell(0).setCellValue("Request");
                rowhead.createCell(1).setCellValue("Duration,s");
                rowhead.createCell(2).setCellValue("Req,count");
            }
            else if(!jMeterResult.getLabel().contains("GET") && !jMeterResult.getLabel().contains("POST"))
            {
                duration = calculateTimings(resultsList, jMeterResult.getLabel());
                HSSFRow row = sheet.createRow((short)i);
                double seconds_duration = Double.valueOf(duration.get(1))/1000;
                if(seconds_duration > test.getSla())
                {
                    CellStyle style = workbook.createCellStyle();
                    style.setFillBackgroundColor(IndexedColors.RED.getIndex());
                    row.setRowStyle(style);
                    Cell cell1 = row.createCell(0);
                    cell1.setCellStyle(style);
                    cell1.setCellValue(jMeterResult.getLabel());
                    Cell cell2 = row.createCell(1);
                    cell2.setCellStyle(style);
                    cell2.setCellValue(seconds_duration);
                    Cell cell3 = row.createCell(2);
                    cell3.setCellStyle(style);
                    cell3.setCellValue(String.valueOf(duration.get(0)));
                    res = true;
                }
                else {
                    row.createCell(0).setCellValue(jMeterResult.getLabel());
                    row.createCell(1).setCellValue(seconds_duration);
                    row.createCell(2).setCellValue(String.valueOf(duration.get(0)));
                }
                i++;
            }
        }
        FileOutputStream fileOut = new FileOutputStream("C:\\jmeter\\apache-jmeter-5.4.1\\bin\\tests\\HTMLReport_" + test.getExecutionTime() + "\\demo" + test.getExecutionTime() + ".xls");
        workbook.write(fileOut);
        fileOut.close();
        workbook.close();
        File file = new File("C:\\jmeter\\apache-jmeter-5.4.1\\bin\\tests\\HTMLReport_" + test.getExecutionTime() + "\\demo" + test.getExecutionTime() + ".xls");
        FileSystemResource file1 = new FileSystemResource(file);
        String message = "";
        if(!res)
        {
            message = "Название теста: " + test.getTestName() + "\nТест URL: " + test.getTestUrl() + "\nRamp Up период: " + test.getRampUp() + " секунд\nДлительность теста: " + test.getDuration() + " секунд\nКоличество пользователей: " + test.getUsers() + "\nSLA: " + test.getSla() + " секунды\nРезультат: " + "\n1. Тестирование соответствует требованям\n2. Процент ошибок: 0.0%";
        }
        else {
            message = "Название теста: " + test.getTestName() + "\nТест URL: " + test.getTestUrl() + "\nRamp Up период: " + test.getRampUp() + " секунд\nДлительность теста: " + test.getDuration() + " секунд\nКоличество пользователей: " + test.getUsers() + "\nSLA: " + test.getSla() + "секунды\nРезультат: " + "\n1. Тестирование не соответствует требованям\n2. Процент ошибок: 0.0%";
        }
        mailSender.sendMail(test.getMail(), subject, message, file1);
    }
    public static List<Integer> calculateTimings(List<JMeter_Results> jMeter_results, String label)
    {
        int average = 0;
        long currentTime = 0L;
        long nextTime = 0L;
        int i = 1;
        int j = 0;
        for(JMeter_Results jMeterResult : jMeter_results)
        {
            if(i < jMeter_results.size()) {
                if (jMeterResult.getLabel().equals(label)) {
                    currentTime = Long.parseLong(jMeterResult.getTimestamp());
                    nextTime = Long.parseLong(jMeter_results.get(i).getTimestamp());
                    average += nextTime - currentTime;
                    j++;
                }
                i++;
            }
        }
        List<Integer> list = new ArrayList<>();
        list.add(j);
        list.add(average/j);
        return list;
    }
}
