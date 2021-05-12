package com.example.Car_Service3.Controllers;

import com.example.Car_Service3.Entity.*;
import com.example.Car_Service3.Repo.*;
import com.example.Car_Service3.Service.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class MainController {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private InsuranceRepository insuranceRepository;
    @Autowired
    private RentalRepository rentalRepository;
    @Autowired
    private VacanciesRepository vacanciesRepository;
    @Autowired
    private MailSender mailSender;
    @RequestMapping(value = "/homepage", method = RequestMethod.GET)
    public ModelAndView getHomepage(){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("homepage");
        modelAndView.addObject(new Issue());
        modelAndView.addObject(new Customer());
        return modelAndView;
    }
    @RequestMapping(value = "/service", method = RequestMethod.GET)
    public String getService(Model model){
        model.addAttribute(new Issue());
        model.addAttribute(new Customer());
        return "service";
    }
    @RequestMapping(value = "/serviceChosen", method = RequestMethod.GET)
    public String getChosenService(@ModelAttribute Issue issueEntity, Model model)
    {
        model.addAttribute(issueEntity);
        model.addAttribute(new Customer());
        return "chosenService";
    }
    @RequestMapping(value = "/chooseService", method = RequestMethod.POST)
    public ModelAndView chooseService(@ModelAttribute Customer customer, @ModelAttribute Issue issue)
    {
        customer.setType("услуга");
        customerRepository.save(customer);
        Service service = setService(customer);
        serviceRepository.save(service);
        issue.setService(service);
        issueRepository.save(issue);
        sendMessage(customer,service,"Запись на услугу СТО");
        return getHomepage();
    }
    @RequestMapping(value = "/insurance", method = RequestMethod.GET)
    public String getInsurancePage(Model model)
    {
        model.addAttribute(new Customer());
        model.addAttribute(new Insurance());
        return "insurance";
    }
    @RequestMapping(value = "/insurance", method = RequestMethod.POST)
    public ModelAndView makeInsurance(@ModelAttribute Customer customer, @ModelAttribute Insurance insurance)
    {
        customer.setType("страхование");
        customerRepository.save(customer);
        Service service = setService(customer);
        serviceRepository.save(service);
        insurance.setService(service);
        insuranceRepository.save(insurance);
        //sendMessage(customer,service,"Запись на страхование СТО");
        return getHomepage();
    }
    @RequestMapping(value = "/rental", method = RequestMethod.GET)
    public String getRentalPage(Model model)
    {
        model.addAttribute(new Customer());
        model.addAttribute(new Rental());
        return "rental";
    }
    @RequestMapping(value = "/rental", method = RequestMethod.POST)
    public ModelAndView RentalCar(@ModelAttribute Customer customer, @ModelAttribute Rental rental)
    {
        customer.setType("бронирование");
        customerRepository.save(customer);
        Service service = setService(customer);
        serviceRepository.save(service);
        rental.setService(service);
        rentalRepository.save(rental);
        //sendMessage(customer,service,"Запись на бронирование авто в СТО");
        return getHomepage();
    }
    @RequestMapping(value = "/vacancies", method = RequestMethod.GET)
    public String getVacanciesPage(Model model)
    {
        model.addAttribute(new Customer());
        model.addAttribute(new Vacancies());
        return "vacancies";
    }
    @RequestMapping(value = "/vacancies", method = RequestMethod.POST)
    public ModelAndView takeVacancy(@ModelAttribute Customer customer, @ModelAttribute Vacancies vacancies)
    {
        customer.setType("вакансия");
        customerRepository.save(customer);
        Service service = setService(customer);
        serviceRepository.save(service);
        vacancies.setService(service);
        vacanciesRepository.save(vacancies);
        //sendMessage(customer,service,"Запись на вакансию в СТО");
        return getHomepage();
    }
    @RequestMapping(value = "/information", method = RequestMethod.GET)
    public String getInformationPage()
    {
        return "information";
    }
    @RequestMapping(value = "/contacts", method = RequestMethod.GET)
    public String getContactsPage()
    {
        return "contacts";
    }

    public void sendMessage(Customer customer, Service service, String subject)
    {
        String message = "Ваша заявка с id: " + service.getService() + ". Принята на исполнение.\nСпасибо Вам, что воспользовались нашими услугами.";
        mailSender.send(customer.getMail(), subject, message);
    }
    public Service setService(Customer customer)
    {
        Service service = new Service();
        service.setService(UUID.randomUUID().toString());
        service.setStatus("open");
        service.setCustomer(customer);
        return service;
    }
}
