//package store.aurora.book.service;
//
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import store.aurora.book.entity.category.Category;
//import store.aurora.book.dto.category.CategoryResponseDTO;
//import store.aurora.book.repository.category.CategoryRepository;
//import store.aurora.book.service.category.impl.CategoryServiceImpl;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.when;
//
//class CategoryServiceTest {
//
//    @Test
//    void testGetCategoryHierarchy() {
//        // Mock CategoryRepository
//        CategoryRepository categoryRepository = Mockito.mock(CategoryRepository.class);
//
//        // Prepare sample data
//        Category root1 = new Category(1L, "123123123", null, 0, null, null);
//        Category child1 = new Category(2L, "22", root1, 1, null, null);
//        Category subChild1 = new Category(3L, "3", child1, 2, null, null);
//        Category subChild2 = new Category(4L, "뽕뽕", child1, 2, null, null);
//        Category child2 = new Category(5L, "파파", root1, 1, null, null);
//        Category child3 = new Category(6L, "이", root1, 1, null, null);
//        Category root2 = new Category(7L, "ㄹ", null, 0, null, null);
//
//        // Mock repository behavior
//        when(categoryRepository.findByParentIsNull()).thenReturn(List.of(root1, root2));
//        when(categoryRepository.findByParentId(1L)).thenReturn(List.of(child1, child2, child3));
//        when(categoryRepository.findByParentId(2L)).thenReturn(List.of(subChild1, subChild2));
//        when(categoryRepository.findByParentId(3L)).thenReturn(List.of());
//        when(categoryRepository.findByParentId(4L)).thenReturn(List.of());
//        when(categoryRepository.findByParentId(5L)).thenReturn(List.of());
//        when(categoryRepository.findByParentId(6L)).thenReturn(List.of());
//        when(categoryRepository.findByParentId(7L)).thenReturn(List.of());
//
//        // Create service
//        CategoryServiceImpl service = new CategoryServiceImpl(categoryRepository);
//
//        // Execute
//        List<CategoryResponseDTO> result = service.getCategoryHierarchy();
//
//        // Verify
//        assertThat(result).hasSize(2);
//        assertThat(result.get(0).getName()).isEqualTo("123123123");
//        assertThat(result.get(0).getChildren()).hasSize(3);
//        assertThat(result.get(0).getChildren().get(0).getName()).isEqualTo("22");
//        assertThat(result.get(0).getChildren().get(0).getChildren()).hasSize(2);
//        assertThat(result.get(0).getChildren().get(0).getChildren().get(1).getName()).isEqualTo("뽕뽕");
//    }
//}
