package co.com.poli.usersservice.controller;

import co.com.poli.usersservice.helpers.Response;
import co.com.poli.usersservice.helpers.ResponseBuild;
import co.com.poli.usersservice.helpers.ResponseFormat;
import co.com.poli.usersservice.persistance.entity.User;
import co.com.poli.usersservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final ResponseBuild builder;

    @GetMapping
    public Response findAll(){
        return builder.success(userService.findAll());
    }

    @PostMapping
    public Response save(@Valid @RequestBody User user, BindingResult result){
        if(result.hasErrors()){
            return builder.failed(ResponseFormat.formatMessage(result));
        }
        userService.save(user);
        return builder.success(user);
    }

    @DeleteMapping("/{id}")
    public Response delete(@PathVariable("id") String id){
        User user = userService.findById(id);
        if(user == null){
            return builder.failed("Usuario no encontrado.");
        }
        Boolean wasDeleted = userService.delete(id);
        if(wasDeleted){
            return builder.success("Usuario eliminado exitosamente!");
        }else{
            return builder.failed("El usuario no se puede eliminar porque tiene reservas asociadas.");
        }
    }

    @GetMapping("/{id}")
    public Response getById(@PathVariable("id") String id){
        User user = userService.findById(id);
        if(user == null){
            return builder.failed("Usuario no encontrado.");
        }
        return builder.success(user);
    }

}
