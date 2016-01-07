package ru.tsystems.javaschool.kuzmenkov.logiweb.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.tsystems.javaschool.kuzmenkov.logiweb.dto.DriverDTO;
import ru.tsystems.javaschool.kuzmenkov.logiweb.entities.Driver;
import ru.tsystems.javaschool.kuzmenkov.logiweb.exceptions.LogiwebServiceException;
import ru.tsystems.javaschool.kuzmenkov.logiweb.exceptions.LogiwebValidationException;
import ru.tsystems.javaschool.kuzmenkov.logiweb.services.CityService;
import ru.tsystems.javaschool.kuzmenkov.logiweb.services.DriverService;
import ru.tsystems.javaschool.kuzmenkov.logiweb.util.CitiesUtil;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Nikolay Kuzmenkov.
 */
@Controller
public class DriverController {

    /**
     */
    @Autowired
    private CityService cityService;
    /**
     */
    @Autowired
    private DriverService driverService;
    /**
     */
    @Autowired
    private CitiesUtil citiesUtil;

    /**
     * @param model model
     * @return DriverList.jsp
     * @throws LogiwebServiceException LogiwebServiceException
     */
    @RequestMapping("driver")
    private String showDrivers(Model model) throws LogiwebServiceException {
        List<Driver> drivers = driverService.findAllDrivers();
        model.addAttribute("drivers", drivers);

        for (Driver driver : drivers) {
            driver.setWorkingHoursThisMonth(driverService
                    .calculateWorkingHoursForDriver(driver.getDriverId()));
        }
        citiesUtil.addAllCitiesToModel(model);

        return "driver/DriverList";
    }

    @RequestMapping(value = {"driver/new"}, method = RequestMethod.GET)
    private String showFormForAddNewDriver(Model model) throws LogiwebServiceException {
        model.addAttribute("formAction", "new");
        model.addAttribute("driverFromForm", new DriverDTO());
        citiesUtil.addAllCitiesToModel(model);

        return "driver/AddOrEditDriver";
    }

    @RequestMapping(value = {"driver/new"}, method = RequestMethod.POST)
    private String addNewDriver(@ModelAttribute("driverFromForm") @Valid DriverDTO driverFromForm,
                               BindingResult result, Model model) throws LogiwebServiceException {
        if (result.hasErrors()) {
            model.addAttribute("driverModel", driverFromForm);
            citiesUtil.addAllCitiesToModel(model);
            model.addAttribute("formAction", "new");

            return "driver/AddOrEditDriver";
        }

        try {
            Integer newDriverId = driverService.addNewDriver(driverFromForm);

            return "redirect:/driver/" + newDriverId;

        } catch (LogiwebValidationException e) {
            model.addAttribute("error", e.getMessage());
            citiesUtil.addAllCitiesToModel(model);
            model.addAttribute("formAction", "new");
            return "driver/AddOrEditDriver";
        }
    }

    /**
     * Removes driver by its ID received in 'driverID' parameter.
     *
     * @throws LogiwebServiceException
     */
    @RequestMapping(value = "driver/{driverId}/delete", method = RequestMethod.POST, produces = "text/plain")
    @ResponseBody
    public String deleteDriver(@PathVariable("driverId") Integer driverId, HttpServletResponse response)
            throws LogiwebServiceException {
        try {
            driverService.deleteDriver(driverId);

            return "Driver deleted";

        } catch (LogiwebValidationException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return e.getMessage();
        }
    }

    @RequestMapping(value = "/driver/{driverId}")
    public String showSingleDriver(@PathVariable("driverId") Integer driverId, Model model) throws LogiwebServiceException {
        //authorizeAccesToDriverInfo(driverId);
        Driver driverToShow = driverService.getDriverWithFullInfo(driverId);

        if (driverToShow == null) {
            throw new LogiwebServiceException();
        }

        if (driverToShow.getCurrentTruckFK() != null) {
            Map<Integer, Driver> coDrivers = new HashMap<>();

            for (Driver coDriverId : driverToShow.getCurrentTruckFK().getDriversInTruck()) {
                coDrivers.put(coDriverId.getDriverId(), driverService.findDriverById(coDriverId.getDriverId()));
            }

            model.addAttribute("coDrivers", coDrivers);
        }

        model.addAttribute("driver", driverToShow);
        model.addAttribute("cities", cityService.findAllCities());

        return "driver/SingleDriver";
    }

    @RequestMapping(value = "order/{orderId}/edit/addDriverToTruck", method = RequestMethod.POST)
    @ResponseBody
    public String addDriverToTruck(@RequestParam("driversIds") Integer[] driversIds, @RequestParam("truckId") Integer truckId,
                                   HttpServletResponse response) throws LogiwebServiceException {
        try {
            for (Integer driverId : driversIds) {
                driverService.assignDriverToTruck(driverId, truckId);
            }

            return "Drivers are added to truck";

        } catch (LogiwebValidationException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return e.getMessage();
        }
    }
}
