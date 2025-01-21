package store.aurora.book.controller.author;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import store.aurora.book.dto.author.AuthorRoleRequestDto;
import store.aurora.book.dto.author.AuthorRoleResponseDto;
import store.aurora.book.service.author.AuthorRoleService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.hasSize;

@ExtendWith(SpringExtension.class)
@WebMvcTest(AuthorRoleController.class)
class AuthorRoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthorRoleService authorRoleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("저자 역할 목록 조회 테스트 - 페이징 검증 (GET /api/author-roles)")
    void getAllRolesTest() throws Exception {
        // given (6개의 데이터로 페이징 테스트)
        List<AuthorRoleResponseDto> mockRoleList = List.of(
                new AuthorRoleResponseDto(1L, "역할 1"),
                new AuthorRoleResponseDto(2L, "역할 2"),
                new AuthorRoleResponseDto(3L, "역할 3"),
                new AuthorRoleResponseDto(4L, "역할 4"),
                new AuthorRoleResponseDto(5L, "역할 5"),
                new AuthorRoleResponseDto(6L, "역할 6") // 6번째 데이터 (2페이지로 넘어감)
        );

        Page<AuthorRoleResponseDto> mockPage = new PageImpl<>(mockRoleList.subList(0, 5), PageRequest.of(0, 5), 6);

        given(authorRoleService.getAllRoles(any(PageRequest.class))).willReturn(mockPage);

        // when & then
        mockMvc.perform(get("/api/author-roles")
                        .param("page", "0")
                        .param("size", "5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 응답 확인
                .andExpect(jsonPath("$.content", hasSize(5))) // 한 페이지에 5개만 있어야 함
                .andExpect(jsonPath("$.totalElements").value(6)) // 전체 데이터 개수 6개
                .andExpect(jsonPath("$.totalPages").value(2)) // 총 페이지 수 2개
                .andExpect(jsonPath("$.content[0].id").value(1L)) // 첫 번째 역할 확인
                .andExpect(jsonPath("$.content[4].id").value(5L)); // 다섯 번째 역할 확인 (한 페이지의 마지막 데이터)

        verify(authorRoleService, times(1)).getAllRoles(any(PageRequest.class));
    }

    @Test
    @DisplayName("특정 저자 역할 조회 테스트 (GET /api/author-roles/{author-role-id})")
    void getRoleByIdTest() throws Exception {
        // given
        Long roleId = 1L;
        AuthorRoleResponseDto mockRole = new AuthorRoleResponseDto(roleId, "역할 1");
        given(authorRoleService.getRoleById(roleId)).willReturn(mockRole);

        // when & then
        mockMvc.perform(get("/api/author-roles/{author-role-id}", roleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // HTTP 200 응답 확인
                .andExpect(jsonPath("$.id").value(roleId))
                .andExpect(jsonPath("$.role").value("역할 1"));

        verify(authorRoleService, times(1)).getRoleById(roleId);
    }

    @Test
    @DisplayName("저자 역할 생성 테스트 (POST /api/author-roles)")
    void createRoleTest() throws Exception {
        // given
        AuthorRoleRequestDto requestDto = new AuthorRoleRequestDto("새 역할");
        doNothing().when(authorRoleService).createRole(any(AuthorRoleRequestDto.class));

        // when & then
        mockMvc.perform(post("/api/author-roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated()); // HTTP 201 응답 확인

        verify(authorRoleService, times(1)).createRole(any(AuthorRoleRequestDto.class));
    }

    @Test
    @DisplayName("저자 역할 수정 테스트 (PUT /api/author-roles/{author-role-id})")
    void updateRoleTest() throws Exception {
        // given
        Long roleId = 1L;
        AuthorRoleRequestDto requestDto = new AuthorRoleRequestDto("수정된 역할");

        doNothing().when(authorRoleService).updateRole(eq(roleId), any(AuthorRoleRequestDto.class));

        // when & then
        mockMvc.perform(put("/api/author-roles/{author-role-id}", roleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isNoContent()); // HTTP 204 응답 확인

        verify(authorRoleService, times(1)).updateRole(eq(roleId), any(AuthorRoleRequestDto.class));
    }

    @Test
    @DisplayName("저자 역할 삭제 테스트 (DELETE /api/author-roles/{author-role-id})")
    void deleteRoleTest() throws Exception {
        // given
        Long roleId = 1L;
        doNothing().when(authorRoleService).deleteRole(roleId);

        // when & then
        mockMvc.perform(delete("/api/author-roles/{author-role-id}", roleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent()); // HTTP 204 응답 확인

        verify(authorRoleService, times(1)).deleteRole(roleId);
    }
}