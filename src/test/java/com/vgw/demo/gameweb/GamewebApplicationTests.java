package com.vgw.demo.gameweb;

import com.vgw.demo.gameweb.gameobj.Player;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class GamewebApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Test
	public void sortTest(){
		ArrayList<Player> sortList = new ArrayList<>();
		for(int i=0;i<5;i++){
			Player ply=new Player();
			ply.setSeatNo(4-i);
			sortList.add(ply);
		}
		Collections.sort(sortList, (a, b) -> a.getSeatNo() < b.getSeatNo() ? -1 : a.getSeatNo() == b.getSeatNo() ? 0 : 1);
		Assert.assertEquals(0,sortList.get(0).getSeatNo());
	}




}
