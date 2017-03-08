package uk.co.smitek.pairstub;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class PairStubApplication {

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(PairStubApplication.class, args);
	}

	@RestController
	@RequestMapping(value = "/pair")
	public static class PairEndpointController {

		private static final Logger log = LoggerFactory.getLogger(PairEndpointController.class);
		@Autowired
		private ObjectMapper objectMapper;

		private List<ReqRes> reqResList = new ArrayList<>();

		@RequestMapping(value = "")
		public String acceptBooking(@RequestBody String json) throws IOException {
			log.debug("Received json: {}", json);

			final JsonNode req = objectMapper.readTree(json);
			JsonNode res = objectMapper.readTree(response(json));


			final ReqRes reqRes = new ReqRes(req, res);

			reqResList.add(reqRes);

			return res.toString();

		}

		private String response(String req) throws IOException {
			if (req.contains("\"passengernum\":4") || req.contains("\"passengernum\": 4")) {
				return objectMapper.readTree("{\n" +
						"  \"response\":{\n" +
						"    \"uniqueid\":\"unique request id\",\n" +
						"    \"bookingcode\":\"TST981\",\n" +
						"    \"status\":\"ERROR\",\n" +
						"    \"description\":\"1002: Out of service\"\n" +
						"  }\n" +
						"}").toString();
			} else {
				return objectMapper.readTree("{\n" +
						"  \"response\":{\n" +
						"    \"uniqueid\":\"unique request id\",\n" +
						"    \"bookingcode\":\"TST981\",\n" +
						"    \"status\":\"OK\",\n" +
						"    \"description\":\"All was good\",\n" +
						"    \"exectime_msec\":45.078992843628\n" +
						"  }\n" +
						"}").toString();
			}
		}

		@RequestMapping(value = "/requests")
		public List<ReqRes> requests() {
			List<ReqRes> copyList = new ArrayList<>(reqResList);
			Collections.reverse(copyList);
			return copyList;
		}

		@RequestMapping(value = "/clear")
		public String clear() {
			reqResList.clear();
			return "OK Rikesh";

		}

	}

	static class ReqRes {

		private String when = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

		private JsonNode req;

		private JsonNode res;

		public ReqRes(JsonNode req, JsonNode res) {
			this.req = req;
			this.res = res;
		}

		public String getWhen() {
			return when;
		}

		public JsonNode getReq() {
			return req;
		}

		public JsonNode getRes() {
			return res;
		}
	}

}
