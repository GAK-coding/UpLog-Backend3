package com.uplog.uplog.product;

import com.uplog.uplog.domain.product.api.ProductController;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

@Transactional
@SpringBootTest
@WebMvcTest(controllers = ProductController.class)
public class ProductServiceTest {
}
