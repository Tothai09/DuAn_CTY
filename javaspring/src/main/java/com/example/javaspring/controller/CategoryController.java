package com.example.javaspring.controller;

import com.example.javaspring.dto.CategoryDto;
import com.example.javaspring.model.Category;
import com.example.javaspring.service.CategoryService;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.support.PagedListHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import java.awt.print.Pageable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    CategoryService categoryService;

    @PreAuthorize("hasAuthority('list_category')")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public List<CategoryDto> getList() {
        List<CategoryDto> categoryDtoList = categoryService.getList();
        return categoryDtoList;
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public CategoryDto findById(@PathVariable long id) {
        CategoryDto category = categoryService.getById(id);
        return category;
    }

    @PreAuthorize("hasAuthority('add_category')")
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    public void postCategory(@RequestBody CategoryDto categoryDto) {
        categoryService.postCategory(categoryDto);
    }

    @PreAuthorize("hasAuthority('edit_category')")
    @RequestMapping(value = "/put/{id}", method = RequestMethod.PUT)
    public void putCategory(@RequestBody CategoryDto categoryDto, @PathVariable long id) {
        categoryService.putCategory(categoryDto, id);
    }

    @PreAuthorize("hasAuthority('delete_category')")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public void deleteCategory(@PathVariable long id) {
        categoryService.deleteCategory(id);
    }

    @GetMapping("/report/{format}")
    public String exportReport(@PathVariable("format") String format) throws JRException, IOException {
        String downloadUri = categoryService.exportReport(format);
        return downloadUri;
    }

    @GetMapping("view/page")
    public String paginate(HttpServletRequest request,
                           @PathVariable int pageNumber, Model model){
        PagedListHolder<?> pages = (PagedListHolder<?>) request.getSession().getAttribute("category-list");
        int pagesize = 3;
        List<CategoryDto> list = (List<CategoryDto>) categoryService.getList();

        System.out.println(list.size());
        if (pages ==null){
            pages=new PagedListHolder<>(list);
            pages.setPageSize(pagesize);
        }else {
            final int goToPage = pageNumber-1;
            if(goToPage<=pages.getPageCount() && goToPage>=0){
                pages.setPage(goToPage);
            }
        }
        request.getSession().setAttribute("category-list",pages);
        int current = pages.getPage() +1;
        int begin = Math.max(1, current-list.size());
        int end = Math.min(begin + 5, pages.getPageCount());
        int totalPageCount = pages.getPageCount();
        String baseUrl = "/category/page";


        model.addAttribute("beginIndex",begin);
        model.addAttribute("endIndex",end);
        model.addAttribute("totalPageCount",totalPageCount);
        model.addAttribute("category",pages);


        return "category-list";

    }

}
