package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);

	}

	@Bean
	Hibernate5Module hibernate5Module() {
		Hibernate5Module hibernate5Module = new Hibernate5Module();


		// 이렇게 강제 지연로딩을 하면 엔티티가 노출이 됩니다 -> 하면 안돼는거예요
		// FORCE_LAZY_LOADING = 지연로딩 돼는 애들을 강제로 조회해버리겠다 !!!! 라는 옵션
//		hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);


		return hibernate5Module;
	}
}
