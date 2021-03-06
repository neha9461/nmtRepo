package com.itt.nmt.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * This is a class responsible to handle all whitelabel error issues.
 * 
 * @author Neha Goyal
 *
 */
@RestController
public class NMTErrorController implements ErrorController {
    /**
     * This is custom implementation of whitelabel issue.
     * when /error is called it will invoke this method,  
     * which in turn redirects to "/" which is index page
     * 
     *@param request , request is a reference of HttpServletRequest object, which we use to forward the request 
     *@param response , response is a reference of HttpServletResponse object, which we use to forward the request
     */
    
    @RequestMapping(value = {"/errorPage" , "/{[path:[^\\.]*}"}, method = RequestMethod.GET)
    public void redirect(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            request.getRequestDispatcher("/index.html").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    /**
     * override getErrorPath of error controller.
     * @return redirect to "/" index page
     */
    @Override
    public String getErrorPath() {
        return "redirect:/";
    }

}
