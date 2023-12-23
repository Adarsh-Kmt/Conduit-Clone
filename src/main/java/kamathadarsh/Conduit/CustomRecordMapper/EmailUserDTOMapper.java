package kamathadarsh.Conduit.CustomRecordMapper;

import kamathadarsh.Conduit.DTO.EmailDTO.EmailUserDTO;
import kamathadarsh.Conduit.jooq.jooqGenerated.tables.records.UserTableRecord;
import org.jetbrains.annotations.Nullable;
import org.jooq.RecordMapper;

public class EmailUserDTOMapper implements RecordMapper<UserTableRecord, EmailUserDTO> {
    @Override
    public @Nullable EmailUserDTO map(UserTableRecord userTableRecord) {

        return EmailUserDTO.builder()
                .emailId(userTableRecord.getEmailId())
                .username(userTableRecord.getUsername())
                .build();
    }

    @Override
    public EmailUserDTO apply(UserTableRecord record) {
        return RecordMapper.super.apply(record);
    }
}
