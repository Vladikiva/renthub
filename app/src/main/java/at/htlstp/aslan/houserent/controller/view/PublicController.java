package at.htlstp.aslan.houserent.controller.view;

import at.htlstp.aslan.houserent.bean.SelectedStationBean;
import at.htlstp.aslan.houserent.service.HouseService;
import at.htlstp.aslan.houserent.service.StationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PublicController {

    private final HouseService houseService;
    private final StationService stationService;

    public PublicController(HouseService houseService, StationService stationService) {
        this.houseService = houseService;
        this.stationService = stationService;
    }

    @GetMapping
    public String showForm(Model model) {
        model.addAttribute("selectedStationBean", new SelectedStationBean());
        model.addAttribute("stations", stationService.findAll());
        return "fragments/search-rentals";
    }

    @PostMapping
    public String processForm(
            Model model,
            @Valid @ModelAttribute("selectedStationBean") SelectedStationBean selectedStationBean,
            BindingResult bindingResult) {
        model.addAttribute("stations", stationService.findAll());
        model.addAttribute(
                "houses",
                bindingResult.hasErrors() ? null : houseService.findByStation(selectedStationBean.getStation()));
        return "fragments/search-rentals";
    }
}
