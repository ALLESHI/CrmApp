package com.alibou.security.service.implementation;

import com.alibou.security.dto.ClientDto;
import com.alibou.security.dto.ProductDto;
import com.alibou.security.model.Client;
import com.alibou.security.model.Product;
import com.alibou.security.repository.ProductRepository;
import com.alibou.security.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class ProductServiceImplementation implements ProductService {
    private final ProductRepository productRepository;
    private final ModelMapper mapper;
    @Override
    public ProductDto create(ProductDto productDto) {
        Product product = dtoToEntity(productDto);
        productRepository.save(product);
        return convertToResponseDto(product);
    }

    @Override
    public Optional<ProductDto> findById(Integer id) {
        return Optional.ofNullable(productRepository.findById(id)
                .map(this::convertToResponseDto).orElseThrow(() ->
                        new UsernameNotFoundException("Product not found with id: " + id)));
    }

    @Override
    public List<ProductDto> findAll() {
        List<Product> products = productRepository.findAll();
        return convertToResponseDto(products);
    }

    @Override
    public ProductDto update(Integer id, ProductDto productDto) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()){
            Product product = productOptional.get();
            product.setName(productDto.getName());
            product.setPrice(productDto.getPrice());
            product.setUnit(productDto.getUnit());
            productRepository.save(product);
            return convertToResponseDto(product);
        }else {
            throw new IllegalStateException("Product not found with id: " + id);
        }
    }

    @Override
    public String deleteById(Integer id) {
        if (productRepository.existsById(id)){
            productRepository.deleteById(id);
            return "Successfully deleted product with id: " + id;
        }else {
            return "Product not found with id: " + id;
        }
    }
    private Product dtoToEntity(ProductDto productDto) {
        return mapper.map(productDto, Product.class);
    }
    private ProductDto convertToResponseDto(Product product){
        return mapper.map(product, ProductDto.class);
    }
    public List<ProductDto> convertToResponseDto(List<Product> products) {
        List<ProductDto> productDtoList = new ArrayList<>();
        for (Product product : products){
            ProductDto productDto = new ProductDto();
            productDto.setId(product.getId());
            productDto.setName(product.getName());
            productDto.setPrice(product.getPrice());
            productDto.setUnit(product.getUnit());
            productDtoList.add(productDto);
        }
        return productDtoList;
    }
}