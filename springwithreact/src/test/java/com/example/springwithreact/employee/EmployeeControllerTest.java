package com.example.springwithreact.employee;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import com.example.springwithreact.api.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = EmployeeController.class)
@Import(GlobalExceptionHandler.class)
class EmployeeControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private EmployeeService employeeService;

	@Test
	void list_returnsEmployees() throws Exception {

		Employee e1 = new Employee("Asha", "Patel", "asha@example.com");
		e1.setId(1L);

		Employee e2 = new Employee("Ravi", "Singh", "ravi@example.com");
		e2.setId(2L);

		when(employeeService.list()).thenReturn(List.of(e1, e2));

		mockMvc.perform(get("/api/employees"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].id").value(1))
				.andExpect(jsonPath("$[0].firstName").value("Asha"))
				.andExpect(jsonPath("$[1].id").value(2))
				.andExpect(jsonPath("$[1].email").value("ravi@example.com"));
	}

	@Test
	void get_returnsEmployee() throws Exception {
		Employee e1 = new Employee("Asha", "Patel", "asha@example.com");
		e1.setId(1L);
		when(employeeService.get(1L)).thenReturn(e1);

		mockMvc.perform(get("/api/employees/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.firstName").value("Asha"));
	}

	@Test
	void get_whenNotFound_returns404ApiError() throws Exception {
		when(employeeService.get(99L)).thenThrow(new EmployeeNotFoundException(99L));

		mockMvc.perform(get("/api/employees/99"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message").value("Employee not found: 99"))
				.andExpect(jsonPath("$.path").value("/api/employees/99"));
	}

	@Test
	void create_valid_returns201() throws Exception {
		Employee created = new Employee("Asha", "Patel", "asha@example.com");
		created.setId(10L);
		when(employeeService.create(any(Employee.class))).thenReturn(created);

		Employee request = new Employee("Asha", "Patel", "asha@example.com");

		mockMvc.perform(post("/api/employees")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(10))
				.andExpect(jsonPath("$.email").value("asha@example.com"));
	}

	@Test
	void create_duplicateEmail_returns400ApiError() throws Exception {
		when(employeeService.create(any(Employee.class))).thenThrow(new IllegalArgumentException("Email must be unique"));

		Employee request = new Employee("Asha", "Patel", "asha@example.com");

		mockMvc.perform(post("/api/employees")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request)))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").value("Email must be unique"))
				.andExpect(jsonPath("$.path").value("/api/employees"));
	}

	@Test
	void create_invalid_returns400WithFieldErrors() throws Exception {
		String body = """
				{
				  "firstName": "",
				  "lastName": "Patel",
				  "email": "not-an-email"
				}
				""";

		mockMvc.perform(post("/api/employees")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").value("Validation failed"))
				.andExpect(jsonPath("$.path").value("/api/employees"))
				.andExpect(jsonPath("$.fieldErrors.firstName").exists())
				.andExpect(jsonPath("$.fieldErrors.email").exists());
	}

	@Test
	void update_valid_callsServiceAndReturnsEmployee() throws Exception {
		Employee updated = new Employee("Asha", "Sharma", "asha@example.com");
		updated.setId(1L);
		when(employeeService.update(eq(1L), any(Employee.class))).thenReturn(updated);

		String body = """
				{
				  "id": 1,
				  "firstName": "Asha",
				  "lastName": "Sharma",
				  "email": "asha@example.com"
				}
				""";

		mockMvc.perform(put("/api/employees/updateEmployee")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.lastName").value("Sharma"));

		verify(employeeService).update(eq(1L), any(Employee.class));
	}

	@Test
	void update_duplicateEmail_returns400ApiError() throws Exception {
		when(employeeService.update(eq(1L), any(Employee.class))).thenThrow(new IllegalArgumentException("Email must be unique"));

		String body = """
				{
				  "id": 1,
				  "firstName": "Asha",
				  "lastName": "Patel",
				  "email": "asha@example.com"
				}
				""";

		mockMvc.perform(put("/api/employees/updateEmployee")
						.contentType(MediaType.APPLICATION_JSON)
						.content(body))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.message").value("Email must be unique"))
				.andExpect(jsonPath("$.path").value("/api/employees/updateEmployee"));
	}

	@Test
	void delete_returns204() throws Exception {
		doNothing().when(employeeService).delete(1L);

		mockMvc.perform(delete("/api/employees/1"))
				.andExpect(status().isNoContent());

		verify(employeeService).delete(1L);
	}

	@Test
	void delete_whenNotFound_returns404ApiError() throws Exception {
		doThrow(new EmployeeNotFoundException(123L)).when(employeeService).delete(123L);

		mockMvc.perform(delete("/api/employees/123"))
				.andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404))
				.andExpect(jsonPath("$.message").value("Employee not found: 123"))
				.andExpect(jsonPath("$.path").value("/api/employees/123"));
	}
}
