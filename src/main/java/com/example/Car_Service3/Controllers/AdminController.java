package com.example.Car_Service3.Controllers;

import com.example.Car_Service3.Entity.*;
import com.example.Car_Service3.Repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

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
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String getAdminPage(Model model)
    {
        Iterable<Customer> customers = customerRepository.findAll();
        model.addAttribute(customers);
        model.addAttribute(new Customer());
        model.addAttribute(new Managers());
        return "admin";
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
    @RequestMapping(value = "/adminService", method = RequestMethod.GET)
    public String getServicePage(Model model)
    {
        ArrayList<Customer> customerList = new ArrayList<>(customerRepository.findCustomersByType("услуга"));
        Iterator<Customer> iterCustomer = customerList.iterator();
        ArrayList<Service> serviceList = new ArrayList<>();
        ArrayList<Issue> issuesList = new ArrayList<>();
        model.addAttribute(customerList);
        while (iterCustomer.hasNext())
        {
            serviceList.add(serviceRepository.findByCustomer(iterCustomer.next()));
        }
        Iterator<Service> iterService = serviceList.iterator();
        while(iterService.hasNext())
        {
            issuesList.add(issueRepository.findIssueByService(iterService.next()));
        }
        model.addAttribute(serviceList);
        model.addAttribute(issuesList);
        model.addAttribute(new Customer());
        model.addAttribute(new Issue());
        return "adminService";
    }
    @RequestMapping(value = "/addService", method = RequestMethod.POST)
    public String addService(@ModelAttribute Customer customer, @ModelAttribute Issue issue, Model model)
    {
        customer.setType("услуга");
        customerRepository.save(customer);
        Service service = new Service();
        service.setService(UUID.randomUUID().toString());
        service.setStatus("open");
        service.setCustomer(customer);
        serviceRepository.save(service);
        issue.setService(service);
        issueRepository.save(issue);
        return getServicePage(model);
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
        return "adminService";
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
        return "adminInsurance";
    }
    @RequestMapping(value = "/addInsurance", method = RequestMethod.POST)
    public String addInsurance(@ModelAttribute Customer customer, Insurance insurance, Model model)
    {
        customer.setType("страхование");
        customerRepository.save(customer);
        Service service = new Service();
        service.setService(UUID.randomUUID().toString());
        service.setStatus("open");
        service.setCustomer(customer);
        serviceRepository.save(service);
        insurance.setService(service);
        insuranceRepository.save(insurance);
        return getInsurancePage(model);
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
    @RequestMapping(value = "/adminInsurance", method = RequestMethod.GET)
    public String getInsurancePage(Model model)
    {
        ArrayList<Customer> customerList = new ArrayList<>(customerRepository.findCustomersByType("страхование"));
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
        return "adminInsurance";
    }
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String deleteService(@RequestParam(name = "service")String serviceId, Model model)
    {
        Service service = serviceRepository.findByService(serviceId);
        Issue issue = issueRepository.findIssueByService(service);
        issueRepository.delete(issue);
        serviceRepository.delete(service);
        customerRepository.delete(service.getCustomer());
        return getServicePage(model);
    }
    @RequestMapping(value = "/deleteInsurance", method = RequestMethod.GET)
    public String deleteInsurance(@RequestParam(name = "service")String serviceId, Model model)
    {
        Service service = serviceRepository.findByService(serviceId);
        Insurance insurance = insuranceRepository.findInsuranceByService(service);
        insuranceRepository.delete(insurance);
        serviceRepository.delete(service);
        customerRepository.delete(service.getCustomer());
        return getInsurancePage(model);
    }
    @RequestMapping(value = "/deleteRental", method = RequestMethod.GET)
    public String deleteRental(@RequestParam(name = "service")String serviceId, Model model)
    {
        Service service = serviceRepository.findByService(serviceId);
        Rental rental = rentalRepository.findRentalByService(service);
        rentalRepository.delete(rental);
        serviceRepository.delete(service);
        customerRepository.delete(service.getCustomer());
        return getInsurancePage(model);
    }
    @RequestMapping(value = "/deleteVacancies", method = RequestMethod.GET)
    public String deleteVacancies(@RequestParam(name = "service")String serviceId, Model model)
    {
        Service service = serviceRepository.findByService(serviceId);
        Vacancies vacancies = vacanciesRepository.findVacanciesByService(service);
        vacanciesRepository.delete(vacancies);
        serviceRepository.delete(service);
        customerRepository.delete(service.getCustomer());
        return getInsurancePage(model);
    }
    @RequestMapping(value = "/beginService", method =  RequestMethod.GET)
    public String beginService(@RequestParam(name = "service")String serviceId, Model model)
    {
        Service service = serviceRepository.findByService(serviceId);
        Issue issue = issueRepository.findIssueByService(service);
        //serviceRepository.delete(service);
        issueRepository.delete(issue);
        service.setStatus("in progress");
        //serviceRepository.save(service);
        issueRepository.save(issue);
        return getServicePage(model);
    }
    @RequestMapping(value = "/finishService", method = RequestMethod.GET)
    public String finishService(@RequestParam(name = "service")String serviceId, Model model)
    {
        Service service = serviceRepository.findByService(serviceId);
        Issue issue = issueRepository.findIssueByService(service);
        //serviceRepository.delete(service);
        issueRepository.delete(issue);
        service.setStatus("finish");
        //serviceRepository.save(service);
        issueRepository.save(issue);
        return getServicePage(model);
    }
    @RequestMapping(value = "/beginInsurance", method = RequestMethod.GET)
    public String beginInsurance(@RequestParam(name = "service")String serviceId, Model model)
    {
        Service service = serviceRepository.findByService(serviceId);
        Insurance insurance = insuranceRepository.findInsuranceByService(service);
        insuranceRepository.delete(insurance);
        service.setStatus("in progress");
        insuranceRepository.save(insurance);
        return getInsurancePage(model);
    }
    @RequestMapping(value = "/finishInsurance", method = RequestMethod.GET)
    public String finishInsurance(@RequestParam(name = "service")String serviceId, Model model)
    {
        Service service = serviceRepository.findByService(serviceId);
        Insurance insurance = insuranceRepository.findInsuranceByService(service);
        insuranceRepository.delete(insurance);
        service.setStatus("finish");
        insuranceRepository.save(insurance);
        return getInsurancePage(model);
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

}
