package web.api.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(web.api.config.RestDocsConfiguration.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @Description("회원 목록 조회")
    public void findAll() throws Exception {
        this.mockMvc.perform(get("/members").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("find-members",
                        responseFields(
                                fieldWithPath("resultCode").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("resultData").type(JsonFieldType.ARRAY).description("응답 데이터"),
                                fieldWithPath("resultData.[].memberId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("resultData.[].name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("resultData.[].birth").type(JsonFieldType.NUMBER).description("회원 생년월일"),
                                fieldWithPath("resultData.[]._links").type(JsonFieldType.OBJECT).description("회원상세 HATEOAS"),
                                fieldWithPath("resultData.[]._links.detail").type(JsonFieldType.OBJECT).description("회원상세 HATEOAS 상세정보"),
                                fieldWithPath("resultData.[]._links.detail.href").type(JsonFieldType.STRING).description("회원상세 HATEOAS 링크"),
                                fieldWithPath("resultData.[]._links.detail.type").type(JsonFieldType.STRING).description("회원상세 HATEOAS HTTP Method"),
                                fieldWithPath("_links").type(JsonFieldType.OBJECT).description("회원목록 HATEOAS"),
                                fieldWithPath("_links.self").type(JsonFieldType.OBJECT).description("회원목록 HATEOAS 상세정보"),
                                fieldWithPath("_links.self.href").type(JsonFieldType.STRING).description("회원목록 HATEOAS 링크")
                        )
                ));
    }

    @Test
    @Description("회원 상세 조회")
    public void findOne() throws Exception {
        this.mockMvc.perform(get("/members/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(document("find-member",
                        responseFields(
                                fieldWithPath("resultCode").type(JsonFieldType.STRING).description("응답 코드"),
                                fieldWithPath("resultData").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("resultData.memberId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                fieldWithPath("resultData.name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("resultData.birth").type(JsonFieldType.NUMBER).description("회원 생년월일"),

                                fieldWithPath("_links").type(JsonFieldType.OBJECT).description("회원상세 HATEOAS"),

                                fieldWithPath("_links.list").type(JsonFieldType.OBJECT).description("회원목록 HATEOAS"),
                                fieldWithPath("_links.list.href").type(JsonFieldType.STRING).description("회원목록 HATEOAS 링크"),
                                fieldWithPath("_links.list.type").type(JsonFieldType.STRING).description("회원목록 HATEOAS HTTP Method"),

                                fieldWithPath("_links.self").type(JsonFieldType.OBJECT).description("회원상세 HATEOAS"),
                                fieldWithPath("_links.self.href").type(JsonFieldType.STRING).description("회원상세 HATEOAS 링크"),
                                fieldWithPath("_links.self.type").type(JsonFieldType.STRING).description("회원상세 HATEOAS HTTP Method"),

                                fieldWithPath("_links.update").type(JsonFieldType.OBJECT).description("회원수정 HATEOAS"),
                                fieldWithPath("_links.update.href").type(JsonFieldType.STRING).description("회원수정 HATEOAS 링크"),
                                fieldWithPath("_links.update.type").type(JsonFieldType.STRING).description("회원수정 HATEOAS HTTP Method"),

                                fieldWithPath("_links.delete").type(JsonFieldType.OBJECT).description("회원삭제 HATEOAS"),
                                fieldWithPath("_links.delete.href").type(JsonFieldType.STRING).description("회원삭제 HATEOAS 링크"),
                                fieldWithPath("_links.delete.type").type(JsonFieldType.STRING).description("회원삭제 HATEOAS HTTP Method")
                        )
                ));
    }

}