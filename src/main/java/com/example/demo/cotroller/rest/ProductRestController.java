package com.example.demo.cotroller.rest;

import java.util.List;
import java.util.Optional;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.exceptions.ProductArleadyExistsException;
import com.example.demo.exceptions.ProductCanNotBeNullException;
import com.example.demo.model.Product;
import com.example.demo.model.dto.ErrorDto;
import com.example.demo.model.dto.IdDto;
import com.example.demo.services.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductRestController {
    final ProductService productService;

    @GetMapping
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }
    // @GetMapping("/{id}")
    // public Product getProduct(@PathVariable Long id) throws ProductCanNotBeNullException{
    //     var product = productService.findProductById(id);
    //     if(product.isPresent()){
    //         return product.get();
    //     }else{
    //         throw new ProductCanNotBeNullException();
    //     }
    // }
    @GetMapping("/{id}")
    public ResponseEntity<Optional<Product>> getProductById(@PathVariable Long id) throws ProductCanNotBeNullException{
        var product = productService.findProductById(id);
        if(product.isPresent()){
            return ResponseEntity.ok(product);
        }else{
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping
    public ResponseEntity<IdDto> addProduct(@Valid @RequestBody Product product) throws ProductArleadyExistsException{
        productService.insertProduct(product);
        return ResponseEntity.ok(
            IdDto.builder()
            .id(product.getId()).build()
        );
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> editCurrentProduct(@PathVariable Long id, @Valid @RequestBody Product product){
        productService.updateCurrentProduct(product, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCurrentProduct(@PathVariable Long id){
        productService.removeProduct(id);
        return ResponseEntity.accepted().build();
    }

    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleExceptio(MethodArgumentNotValidException exception){
        var errors = exception.getAllErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ErrorDto.builder()
            .message(errors).build()
        );
        
    }
}
