package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.*;

import java.util.List;
import ru.kata.spring.boot_security.demo.service.RoleService;


@Controller
public class AdminController {

    private final UserServiceImp userService;
    private final RoleService roleService;


    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {

        return new HiddenHttpMethodFilter();
    }

    @Autowired
    public AdminController(UserServiceImp userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/admin")  //Отображает список пользователей в административной панели
    public String showListUsers(Model model) {
        List<User> users = userService.listUsers();
        model.addAttribute("users", users);
        return "admin";
    }

    @GetMapping("/admin/new")  //Отображает форму добавления нового пользователя
    public String showAddUserForm(Model model) {
        User user = new User();
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.listRoles());
        return "addUser";
    }

    @PostMapping("/admin/add")  //Обрабатывает запрос на добавление нового пользователя
    public String addUser(@ModelAttribute("user") User user) {
        userService.add(user);
        return "redirect:/admin";
    }

    @GetMapping("/admin/edit")  //Отображает форму редактирования пользователя по его ID
    public String showEditUserForm(@RequestParam("id") Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.listRoles());
        return "editUser";
    }

    @PutMapping("/admin/update")  //Обрабатывает запрос на обновление информации о пользователе
    public String updateUser(@ModelAttribute("user") User user) {
        userService.update(user);
        return "redirect:/admin";
    }

    @DeleteMapping("/admin/delete")  //Обрабатывает запрос на удаление пользователя по его ID.
    public String deleteUser(@RequestParam("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}
