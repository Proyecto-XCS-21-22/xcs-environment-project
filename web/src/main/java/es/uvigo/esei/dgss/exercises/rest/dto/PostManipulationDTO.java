package es.uvigo.esei.dgss.exercises.rest.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = As.WRAPPER_OBJECT)
@JsonSubTypes({
	@Type(name = "link", value = LinkManipulationDTO.class),
	@Type(name = "photo", value = PhotoManipulationDTO.class),
	@Type(name = "video", value = VideoManipulationDTO.class)
})
public abstract class PostManipulationDTO {}
