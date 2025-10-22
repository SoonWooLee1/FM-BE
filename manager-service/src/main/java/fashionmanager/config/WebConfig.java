package fashionmanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${filepath}")
    private String uploadPath;

    // yml에서 설정값 주입 — 기본값은 현재 실행 경로(user.dir)/uploadFiles/
    @Value("${file.upload-root:${user.dir}/uploadFiles}")
    private String uploadDir;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**").addResourceLocations("file:///"+uploadPath);

        // /files/** 요청 → 실제 파일 경로로 매핑
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:///" + uploadDir + "/");
    }
}
