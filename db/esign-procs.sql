delimiter ;

drop procedure if exists p_get_doc_esign_info ;

delimiter //

create procedure p_get_doc_esign_info(v_doc_uuid varchar(50))
begin
  SELECT 
    id,
    doc_uuid,
    context_id,
    doc_type,
    doc_path,
    pdf_doc_path,
    doc_template_code,
    encrypted,
    upload_date,
    doc_content,
    doc_sign_date,
    time_zone_id,
    locale_pref
FROM
    doc_esign_master
WHERE
    doc_uuid = v_doc_uuid;
end; //

delimiter ;

drop procedure if exists p_update_doc_esign_info ;

delimiter //

create procedure p_update_doc_esign_info(
        in v_doc_uuid varchar(50),
	in v_context_id varchar(36), 
	in v_doc_type   varchar(30),
	in v_doc_path varchar(500),  
	in v_pdf_doc_path varchar(500), 
	IN v_doc_code varchar(30),
	IN v_content mediumtext,
	in v_doc_sign_date datetime,
	in v_time_zone_id varchar(50),
	in v_locale_pref varchar(10),
	OUT o_id int)
begin
   IF NOT EXISTS (select 1 from doc_esign_master where doc_template_code = v_doc_code and doc_uuid = v_doc_uuid) THEN
	insert into doc_esign_master (doc_uuid, context_id, doc_type, doc_path, pdf_doc_path, doc_template_code, upload_date, doc_content, doc_sign_date, time_zone_id, locale_pref) 
	values (v_doc_uuid, v_context_id, v_doc_type, v_doc_path, v_pdf_doc_path, v_doc_code, current_timestamp(), v_content, v_doc_sign_date, v_time_zone_id, v_locale_pref);
    ELSE 
	UPDATE doc_esign_master SET pdf_doc_path=v_pdf_doc_path, doc_path=v_doc_path, upload_date = current_timestamp(), doc_content = v_content,  doc_sign_date = v_doc_sign_date
	WHERE doc_template_code = v_doc_code and doc_uuid = v_doc_uuid;
    END IF;
    select id into o_id from doc_esign_master where doc_template_code = v_doc_code and doc_uuid = v_doc_uuid;
end; //

delimiter ;

drop procedure if exists p_create_doc_esign_device_info ;

delimiter //

create procedure p_create_doc_esign_device_info(
	IN i_device_id varchar(45),
    	IN i_doc_id  int(11),
    	IN i_platform varchar(30),
    	IN i_manufacturer varchar(30),
    	IN i_model varchar(30),
    	IN i_os_version varchar(30),
	IN i_ip_address varchar(30)
)
begin
   insert into doc_esign_device (device_id, doc_id, platform, manufacturer, model, os_version, ip_address) values 
			(i_device_id, i_doc_id, i_platform, i_manufacturer, i_model, i_os_version, i_ip_address);
end; //