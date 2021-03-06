package ssh2.springboot_ssh_client.controller.question;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ssh2.springboot_ssh_client.controller.question.dto.Response_findById_question_dto;
import ssh2.springboot_ssh_client.controller.question.dto.Response_marking_question_dto;
import ssh2.springboot_ssh_client.question.QuestionDomain;
import ssh2.springboot_ssh_client.question.service.Question_general_Service;
import ssh2.springboot_ssh_client.question.service.Question_rest_service;
import ssh2.springboot_ssh_client.sshclient.SshClientHandler;

@RestController
@RequiredArgsConstructor
@Slf4j
public class Question_APIController {
    private final Question_rest_service question_rest_service;
    private final Question_general_Service question_general_service;
    private final SshClientHandler sshClientHandler;

    @GetMapping("/api/v1/question/{id}")
    public Response_findById_question_dto get_question(@PathVariable Long id){
        Response_findById_question_dto response = question_rest_service.findById(id);
        return response;
    }

    /***
     * 문제 채점
     * @return
     */
    @GetMapping("/api/v1/question/marking/{id}")
    public Response_marking_question_dto marking(@PathVariable Long id){
        QuestionDomain find_question = question_general_service.findById(id);
        String answer = "";

        sshClientHandler.Init();
        String results = sshClientHandler.send_command(find_question.getMarking_command());
        log.info("채점 명령어 실행 결과: " + results);
        sshClientHandler.disconnect();

        if(results.isEmpty()){
            answer = "incorrect";
        }else{
            answer = "correct";
        }

        return Response_marking_question_dto.builder()
                .results(answer)
                .build();
    }

}
