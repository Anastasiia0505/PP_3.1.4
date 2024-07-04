package ru.kata.spring.boot_security.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserServiceImp;

import java.util.List;

@Controller
public class AdminController {

    private final UserServiceImp userService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {

        return new HiddenHttpMethodFilter();
    }

    @Autowired
    public AdminController(UserServiceImp userService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
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

    @PutMapping("admin/update")
    public String updateUser(@ModelAttribute("user") User updatedUser, @RequestParam(value = "newPassword", required = false) String newPassword) {
        User existingUser = userService.findById(updatedUser.getId());

        // Если newPassword не null и не пустой, обновляем пароль
        if (newPassword != null && !newPassword.isEmpty()) {
            // Хешируем новый пароль перед сохранением
            String hashedPassword = passwordEncoder.encode(newPassword); // предполагается, что у вас есть passwordEncoder
            existingUser.setPassword(hashedPassword);
        }

        // Обновляем остальные поля пользователя
        existingUser.setName(updatedUser.getName());
        existingUser.setSurname(updatedUser.getSurname());
        existingUser.setSalary(updatedUser.getSalary());
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setRoles(updatedUser.getRoles());

        userService.update(existingUser);
        return "redirect:/admin";
    }


    @GetMapping("/admin/view")
    public String showViewUserForm(@RequestParam("id") Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        model.addAttribute("allRoles", roleService.listRoles());
        return "viewUser";  // Переименуйте шаблон на "viewUser.html"
    }

    @DeleteMapping("/admin/delete")  //Обрабатывает запрос на удаление пользователя по его ID.
    public String deleteUser(@RequestParam("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}