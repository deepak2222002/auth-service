package web.minda.project.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import web.minda.project.entity.LoginMaster;
import web.minda.project.entity.MasterDetails;
import web.minda.project.repositories.LoginMasterRepository;
import web.minda.project.repositories.MasterDetailsRepository;

import web.minda.project.security.JwtHelper;

@Controller
@RequestMapping("/auth")
public class WebPageController {

	@RequestMapping("/loginpage")
	public String forwardToReact() {
		return "forward:/projectLoginpage/index.html";
	}
	
	@RequestMapping("/createAccount")
	public String forwardTocreateAccount() {
		return "forward:/projectLoginpage/index.html";
	}

	@RequestMapping(value = { "/employeeDashboard" })
	public String forwardToDashboard() {
		return "forward:/projectEmployeeDashboard/index.html";
	}
    

}
