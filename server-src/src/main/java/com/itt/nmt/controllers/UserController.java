package com.itt.nmt.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.itt.nmt.models.User;
import com.itt.nmt.request.models.UserRequst;
import com.itt.nmt.response.models.ResponseMsg;
import com.itt.nmt.services.UserService;
import com.itt.utility.Constants;

/**
 * This class is responsible for exposing REST APis for User.
 */
@RestController
@RequestMapping(value = "/users")
public class UserController {
    /**
     * Service implementation for DB entity that provides retrieval methods.
     */
    @Autowired
    private UserService userService;

    /**
     * REST API to add a new User.
     *
     * @param userRequest the user request
     * @param result the result
     * @return the model map
     */
    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ModelMap add(@Valid
            @RequestBody
            final UserRequst userRequest, final BindingResult result) {

        User user = userRequest.getUser();
        String errorMsg = userService.validateUser(user, result);
        if (errorMsg != null && !errorMsg.isEmpty()) {

            ResponseMsg postResponseMsg = new ResponseMsg(false, errorMsg);
            return new ModelMap().addAttribute("success", postResponseMsg);
        }

        userService.save(user);
        ResponseMsg postResponseMsg = new ResponseMsg(true, Constants.USER_ADDED_SUCCESS_MSG);
        return new ModelMap().addAttribute("success", postResponseMsg);
    }
    /**
     * REST Interface for user retrieval.
     * @param request request sent.
     * @param id id of the entity.
     * @return ModelMap.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ModelMap getUser(final HttpServletRequest request, @PathVariable("id") final String id) {
        User user = userService.getUserByID(id);
        return new ModelMap().addAttribute("user", user);
    }
    /**
     * REST Interface for users retrieval.
     * @param request request sent.
     * @param pageablePage page containing users.
     * @return Page page of users.
     */
    @RequestMapping(method = RequestMethod.GET)
    public Page<User> getAllUsers(final HttpServletRequest request,
            @PageableDefault(value = Constants.PAGE_SIZE)final Pageable pageablePage) {
        return userService.getAllUsers(pageablePage);
    }
    /**
     * REST API to update a User.
     *
     * @param userRequest the user request
     * @param result the result
     * @param id Id of the user to be updated.
     * @param httpServletRequest HttpServletRequest.
     * @return ModelMap.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = "application/json")
    public ModelMap updateUser(@Valid @RequestBody


    final UserRequst userRequest, final BindingResult result, @PathVariable("id")
    final String id, final HttpServletRequest httpServletRequest) {

        User user = userRequest.getUser();
        String errorMsg = userService.validateUser(user, result);
        if (errorMsg != null && !errorMsg.isEmpty()) {
            ResponseMsg postResponseMsg = new ResponseMsg(false, errorMsg);
            return new ModelMap().addAttribute("success", postResponseMsg);
        }
        userService.updateUser(user, id, httpServletRequest.getHeader(Constants.AUTHORIZATION));
        ResponseMsg updateResponseMsg = new ResponseMsg(true, Constants.DEFAULT_UPDATE_SUCCESS_MSG);
        return new ModelMap().addAttribute("success", updateResponseMsg);

    }
}