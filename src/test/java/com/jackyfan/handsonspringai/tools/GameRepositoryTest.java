package com.jackyfan.handsonspringai.tools;

import com.jackyfan.handsonspringai.service.GameRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GameRepositoryTest {
    @Autowired
    private GameRepository gameRepository;
    @Test
    public void test(){
        long count  = gameRepository.count();
        System.out.println("count:"+count);
    }
}
