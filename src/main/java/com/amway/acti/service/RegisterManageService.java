package com.amway.acti.service;

        import com.amway.acti.model.Register;
        import com.google.zxing.WriterException;
        import org.springframework.validation.annotation.Validated;

        import javax.validation.constraints.NotNull;
        import java.io.IOException;
        import java.util.List;

@Validated
public interface RegisterManageService {
    List<Register> findRegisterList(Register register, Integer pageNo, Integer pageSize);

    List<Register> queryRegisterList(Register register);

    void batchRegisterUp(@NotNull(message = "未选择学员") String uIds, @NotNull(message = "课程号为空") String courseId);

    String codeCreate(@NotNull(message = "课程号为空")String courseId) throws WriterException, IOException;

}
