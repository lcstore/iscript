package com.lezo.iscript.yeam;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ScriptableObject;
import org.w3c.dom.Document;

public class UABuilder {
	private static final List<String> uaSeeds = new ArrayList<String>();
	private static ScriptableObject scope = null;
	static {
		// uaSeeds.add("252fCJmZk4PGRVHHxtOZXsnZHo9ay11PWsQKg%3D%3D%7CfyJ6Zyd9OWIhY3YiZ3UoaRg%3D%7CfiB4D157YHtufDUqfHY4fmo7dCQaAxlbU1AFS2IT%7CeSRiYjNhIHA2cGU0c2Y7fGgsdTdwNXJlO39rM35lIXE3bSxqej8W%7CeCVoaEATTRBZGBJEAghGAwNCCFMBDAUBOxI%3D%7Cey93eSgW%7Cei93eSgW%7CdSpyBUAQTBBZFAtVGQtQEA1MHlwMSBAFUQkeRwMcQxdQCVUQA1cVH0sJEU4cWwFHHwFVFm1c%7CdCltbTwERRRWEwVOZmI%2FeG0reTtlJ2R3IWNyYiYkZTF6KmgoPGIlOmIjPXwpal8NVUJ4LlU%3D%7Cdy93AFEQThBRERtIBRtbHgpIEzwI%7Cdi52AVARTx9eGRNDBxdXEApIEzwI%7CcSlxBlcWSBBREwRPDRxFHQBBEFMKJw4%3D%7CcClxBlcWSHo7fmAwaHgnZxZJGkQPXQUSRwQWSmMS%7CcypyBVQVS3k6fG8kYX4kDQ1MA1MYRAgXQgQTTQ5%2FUQ%3D%3D%7CcitzBFUUSng6eWwnYnArAgJDDFwXSwcYTQoYQQNyXA%3D%3D%7CbTdvGElbC04PTEYRSV4CRVkfTw9Tflc%3D%7CbDZuGUhaCk8PV0EKT1kGQlgcRwQwHQ%3D%3D%7CbzdzbzBjPWY6f207Y3YvbGwqcC5wNW15Mn9rK2hoLWIlYCFmbDp8ditubi99I3g4EQ%3D%3D");
		uaSeeds.add("235fCJmZk4PGRVHHxtEb3EtbnA3YSd/N2EaIA==|fyJ6Zyd9OWEobH0tYXIubh8=|fiB4D150Q1JSSgMWB1MdRUsBQB5Vcm8nJD9ucSdWeA==|eSRiYjNhIHA2c2w7eGk9fGAnfjx7OX5uMXNkPHpmJ3w9bS9qeCx6AQ==|eCVoaEATTRdeHxVAGBxbW1V7RQ==|ey93eSgW|ei93eSgW|dShtbUUEHgMHA1YLUFlZQgkTHkJfTlURFwwECxBRDQMUEAITRw8XXlVICldeRFlEXEVRUQ4OHw4QGgYGFw9FBBZFRV0uLyk/Pz0saXozYX9+N2JiY3lWUQMdBUYXBl5MQBQZDB5ZS1MBMippdHBdWEdUGxJbTAYUSlZYFxsfCkMCE1tCRldQQUZKHBxKQWo+NydiY2V0aUZHExhRHwcHWw1cG0FwITVnIUEITlcWMy0rPCFpIDFjbnV3Z2t0OHo7Lw4WX1RJCxUfCU5WVh0WR1MKTScmIDY2NCVgc1gLFQ0ERRNSf2dnNSJzZz54YSEDVA9KDB9KeHslZHo8bVlaTBZVRlMaWE8MElQUSApLX34vO2IkPX1YCE0fSTII|dCtzBEUUVBFWFwhDBBZIEAlKGUcfXB4ORQYWQhoHRxRSF1ASDDYf|dyptbUUEISE/L2Z2cXcsenorHyQDUxYCSQ0HWwMYXB0yBg==|ditvbz4GRhtSHg9EbGg1cmcgez5gJ2R0JWN2ZiIgYzRzL2kqPGsuO2IjOHgpYzIeSEIUPTkI|cStzBFViQGVKTgkdGktITgYqeChtJWVvOGB3K2l0Mw0i|cCpyBVRjQWRLTwgcG0pJTwcreSlsKmthNm55JWh1Mgwj|cylxBldgQmdITAsfGElKTAQoeipvKGVvOGB3KmxxNwkm|cihwB1ZhQ2ZJTQoeGUhLTQUpeytuKGpgN294JWR9Nwkm|bTdvGEl+XHlWUhUBBldUUho2ZDRxN3Z8K3NkOXpvJRs0|bDZuGUh/XXhXUxQAB1ZVUxs3ZTVwN3pwJ39oNnNnJBo1|bzVtGkt8XntUUBcDBFVWUBg0ZjZzMnB6LXViOHRqLhA/|bjRsG0p9X3pVURYCBVRXURk1ZzdyM3F7LHRjOH9hKhQ7|aTNrHE16WH1SVhEFAlNQVh4yYDB1NHZ8K3NkP3hiKBY5|aDJqHUx7WXxTVxAEA1JRVx8zYTF0NXd9KnJlPn5gJBo1|azFpHk94Wn9QVBMHAFFSVBwwYjJ3NnR+KXFmPX1lLhA/|ajBoH055W35RVRIGAVBTVR0xYzN2N3V/KHBnPH1hKxU6|ZT9nEEF2VHFeWh0JDl9cWhI+bDx5OHpwJ39oM3JtLxE+|ZD5mEUB3VXBfWxwID15dWxM/bT14OXtxJn5pMnNmJhg3|Zz1lEkN0VnNcWB8LDF1eWBA8bj57OnhyJX1qMXNuLBI9|ZjxkE0J1V3JdWR4KDVxfWRE9bz96P3txInptNnRrKhQ7|YTtjFEVyUHVaXhkNCltYXhY6aDh9OHtxJn5pPXtnJxk2|YDpiFURzUXRbXxgMC1pZXxc7aTl8PX91InptOXtmLRM8|YzlhFkdwUndYXBsPCFlaXBQ4ajp/OHpwI3tsOHRrIB4x|YjhgF0ZxU3ZZXRoOCVhbXRU5azt+OHV/KHBnMndsJxk2|XQdfKHlObElmYiUxNmdkYioGVARBCExGEUleC0pREC4B|XAZeKXhPbUhnYyQwN2ZlYysHVQVAB0pAF09YDUFfHyEO|XwRcK3pNb0plYSYyNWRnYSkFVwdCAlpOG19PGjNC|XgRcK3pNb0plYSYyNWRnYSkFVwdCA0BKHUVSB0BcH0VqXg==|WQNbLH1KaE1iZiE1MmNgZi4CUABFBEdNGkJRD0NZHkxjVw==|WAJaLXxLaUxjZyA0M2JhZy8DUQFEBUZMG0NQBUlUFUFuWg==|WwJaLXxVeEJHURoUFGF5bDQuLn47EFVDE0tfAkU0azhmLX8nN2MuOmclVHo=|WgFZLn9We0FEUhkXF2J6bzctLX04eSExZSg8YiFQfg==|VQ1JVQpZB1wARVcBWUwVVlYQShRKD1dAFlZcAVlEAVIMXBxEUAJaThZOUBBfH0QYXkkCRFISV0IdTwVAAUJIHlxWC0pKCF8BWRpCVQBYTxJKVxRbEFUdRVQfXVcOVk4RQwdCAFhPBEBKC05QD10eKg==");
		uaSeeds.add("096fCJmZk4PGRVHHxtOZXsnZHo9ay11PWsQKg%3D%3D%7CfyJ6Zyd9OWImZHssaXknYRA%3D%7CfiB4D157YHtufDUqfHY4fmo7dCQaAxlbU1AFS2IT%7CeSRiYjNhIHA2cGIzfmg1d2kocTN0N3JlO3toNXBsJ3czbSxqdCdxCg%3D%3D%7CeCVoaEAQThZWEg1GAxZWAEJ8OGY7ZyItF0sfGDc5Fyk%3D%7Cey93eSgW%7Cei93eSgW%7CdSpyBUAQTBBZFAtVGQtQEA1MHlwMSBAFUQkeRgoQTx9eBUUdClgfD08DGV4RUg5NDwVaHQw9FA%3D%3D%7CdCltbTwERRRWEwVOZmI%2FeG0reTtlJ2R3IWNyYiYkZTF6KmgoPGIlOmIjPXwpal8NVUJ4LlU%3D%7Cdy93AFEQThBTEBpJDRhYHgFHHDMH%7Cdi52AVARTxFUGBJHBRVVFw9EHjEF%7CcSlxBlcWSBlYHhRCBBNTEA9FFToO%7CcClxBldFFVB7Pih2ND5lKVgHVApBE0tUAUVaa0I%3D%7CcypyBVRGQU1PQklMTARKEmUndTBsMHxpUwsdXQsFWghKElMTaFI%3D%7Ccipuci1%2BIHsnYnAmfmsycXE3bTNtKHBkL2J2NnV1MH84fTx7cSdhGg");
		uaSeeds.add("178fCJmZk4PGRVHHxtOZXsnZHo9ay11PWsQKg%3D%3D%7CfyJ6Zyd9OWImZHsrbXolZRQ%3D%7CfiB4D157YHtufDUqfHY4fmo7dCQaAxlbU1AFS2IT%7CeSRiYjNhIHA2cGIzfm8xdGovdjRzM3FuPHxtNnRuLXk%2Bbixvfit9Bg%3D%3D%7CeCVoaEAQThZUFgFKDxtbDVMAAB5CXxZrJGA%2BYTdGaA%3D%3D%7Cey93eSgW%7Cei93eSgW%7CdSpyBUAQTBBZFAtVGQtQEA1MHlwMSBAFUQkeRgoQTxhdAV0YC18dF0MBGUYUUwlPFwldHmVU%7CdCltbTwERRRWEwVOZmI%2FeG0reTtlJ2R3IWNyYiYkZTF6KmgoPGIlOmIjPXwpal8NVUJ4LlU%3D%7Cdy52AVBARk5MTBocD0wCWi1sPXg9fjIlH0dREUdJFkQEXhZRKhA%3D%7Cdi52AVARTxRTEBpODx9fGgVGElltQA%3D%3D%7CcShwB1YXSXs4f2gjZHMuBwdEC1sQTAkbTg4YKQA%3D%7CcChscC98InklYHIkfGkwc3M1bzFvKnJmLWB0NHFrNGE%2FZyMK");
		// uaSeeds.add("120fCJmZk4PGRVHHxtOZXsnZHo9ay11PWsQKg%3D%3D%7CfyJ6Zyd9OWImZXMnYHIoaxo%3D%7CfiB4D157YHtufDUqfHY4fmo7dCQaAxlbU1AFS2IT%7CeSRiYjNhIHA2cGIydmM8fGYiezl%2BPX1qOHlsMHRuLn40ZCNidiJ0Dw%3D%3D%7CeCVoaEATTRZVGBJNFRFVEUUCVxRXF0E6AA%3D%3D%7Cey93eSgW%7Cei93eSgW%7CdSpyBUAQTBBZFAtVGQtQEA1MHlwMSBAFUQkeRgoQTx5eA0EZDlwbC0sHHVoVVgpJCwFeGQg5EA%3D%3D%7CdCltbTwERRRWEwVOZmI%2FeG0reTtlJ2R3IWNyYiYkZTF6KmgoPGIlOmIjPXwpal8NVUJ4LlU%3D%7Cdy93AFEQTh9cGxFEAhxcGQNHFDsP%7Cdi52AVARTxJTHxVGBBRUEg5EFzgM%7CcSlxBlcWSBlbHhRCBhRUEQpMH1tvQg%3D%3D%7CcClxBldFFVB7NyJyKjRoQUECTR1WCk9RB0BRYEk%3D%7CcylxBldFFVATVV8IUEcSUEoOMB8%3D%7CcihwB1ZEFFEZQVccWkwVUERqVA%3D%3D%7CbTdvGElbC04GXkgDRVMITFR6RA%3D%3D%7CbDVtGksKVGYhbH80cmw0HR1eEUEKVhAAURUKOxI%3D%7CbzZuGUhaXVFTXlVQUBhWDnk7aSNzL2x6QBgOThgWSRhSCkIDeEI%3D%7CbjZybjFiPGc7fmw6YncubW0rcS9xNGx4M35qKmlpLGMkYSBnbTh%2FdSttbSx%2BIHAsYBs%3D");
		uaSeeds.add("041fCJmZk4PGRVHHxtIY30hYnw7bStzO20WLA==|fyJ6Zyd9OWImZXImZHAsahs=|fiB4D157YHtufDUqfHY4fmAxfi4QCRNRWVoPQWgZ|eSRiYjNhIHA2cGIyd2I4fWkocTN0NnFnMHxiP3hkIXYxaStseTwV|eCVoaEATTRVXEBpOFhJNXEgZJwg=|ey93eSgW|ei93eSgW|dSpyBUMVSxJOCxhOAwlWEAhXBUIYXgYYSQkDXh8ARQpAGl10Dw==|dCltbTwERRRWEwVOZmI/eG0reTtlJ2R3IWNyYiYkZTF6KmgoPGIlOmIjPXwpal8NVUJ4LlU=|dy52AVBCEld8PiB/JTRhLDlyJmMzcTwsczA6ZCU7FVoYXQ9ZUw1OUQQtXA==|dix0A1JAEFUUV10KUkUYXkEKNBs=|cSlxBldFFVAWWkQNTlEESV0ZTQZdFFZIGEBUDUtLCV4ZQAUsVw==|cCpyBVRGFlMRV0gDR00QUEoMX3BE|cylxBldFFVASVEsARE4TUkkJUn1J|cipyBVRGFlMbW0wFRFsOQ1geSAJeF1BBE0tfB0RGBF4VRQxOUQJOWgJEWgVUFU0FQDsB|bTRsG0pYX1NRXFdSUhpUDHsxaitsLWB/IWB3Kml0P2ssaStreTBzbDl0YCRwO2Apa3UlZB9fGxtKC1UOTQEUS2IZ|bDRwbDNgPmU5fG44YHUsb28pcy1zNm56MXxoKG13KHk9eD1lcSJ6bjFpdyh6OAw=");
		uaSeeds.add("243fCJmZk4PGRVHHxtIY30hYnw7bStzO20WLA==|fyJ6Zyd9OWImZXIjZngtbRw=|fiB4D157YHtufDUqfHY4fmAxfi4QCRNRWVoPQWgZ|eSRiYjNhIHA2cGIyd2c6dmIkfT94MXFnMHdoPXlgJ3M1ayxqfTgR|eCVoaEAQThZUEgNIDRNTBQBeWUcVEwZSFQ1YV1tKYnRhYjAZYg==|ey93eSgW|ei93eSgW|dSpyBUMVSxJOCxhOAwlXEQ9KBUYaWRsRTgweXhsCQhdJGFsYY1k=|dCltbTwERRRWEwVOZmI/eG0reTtlJ2R3IWNyYiYkZTF6KmgoPGIlOmIjPXwpal8NVUJ4LlU=|dy93AFEQThJUGBBADRJHCxBUDk4eWBQFTggaThQPRR9UBUIBHksGFkIBGEcXUAFBBX5E|di52AVARTxNWEBpPAxVXFAFLEVsFQg8bRQcZQgMDQxZSCElgGw==|cSlxBlcWSBdTEBhNCR9DBhhZCk8WVRAETwkZTRcMRhxXBkECHUgFFUECG0QUUQpLCXJI|cClxBldFFVB7NyN9JzRhLDl9K2w9eDUgcDE7YSMhZjV3Lm4rOG0sMmklMB5RE1YEUlgMQVUPTj8R|cylxBldFFVAYQFYdXUsQVk5gXg==|cihwB1ZEFFEZQVccXEoeUkpkWg==|bTdvGElbC04MSlUeWlAITVMRQ2xY|bDVtGktZXlJQXVZTUxtVDXo4aSt6Jmp/NnNsOXRhJH44aStrfylkH18bG0oLVQhNDxlPZh0=|bzdzbzBjPWY6f207Y3YvbGwqcC5wNW15Mn9rK2hoLWIlYCFmbDl+dCpsbC1/IXFc");
		// uaSeeds.add("175fCJmZk4PGRVHHxtOZXsnZHo9ay11PWsQKg%3D%3D%7CfyJ6Zyd9OWImZXEhZnMqaRg%3D%7CfiB4D157YHtufDUqfHY4fmo7dCQaAxlbU1AFS2IT%7CeSRiYjNhIHA2cGIydGU6fWUvdjRzOnlrPn9qN3dsLHYway1ufjsS%7CeCVoaEAQThZWFAFKDAZIUVUEOhU%3D%7Cey93eSgW%7Cei93eSgW%7CdSpyBUAQTBBZFAtVGQtQEA1MHlwMSBAFUQkeRwMcQxJRC1cSAVUXHUkLE0weWQNFHQNXFG9e%7CdC52AVARTxdWFB5JEQZTH25A%7Cdy11AlMSTBRVFx1KEgZaG2pE%7Cdi5qdil6JH8jZnQiem82dXUzaTdpLHRgK21zM3VuQA%3D");
		uaSeeds.add("120fCJmZk4PGRVHHxtOZXsnZHo9ay11PWsQKg%3D%3D%7CfyJ6Zyd9OWImZXEhZnkhZxY%3D%7CfiB4D157YHtufDUqfHY4fmo7dCQaAxlbU1AFS2IT%7CeSRiYjNhIHA2cGIydGU6d2krcjB3MHBuMHVgPX1oLXw4ZC1teD0U%7CeCVoaEAQThNTEhhJERVCVRYLHAdMYUg%3D%7Cey93eSgW%7Cei93eSgW%7CdSpyBUAQTBBZFAtVGQtQEA1MHlwMSBAFUQkeRgoQTx5eBUQcC1keDk4CGF8QUw9MDgRbHA08FQ%3D%3D%7CdCltbTwERRRWEwVOZmI%2FeG0reTtlJ2R3IWNyYiYkZTF6KmgoPGIlOmIjPXwpal8NVUJ4LlU%3D%7Cdy93AFEQTh9YGBJHCx1dGwJJH1hsQQ%3D%3D%7Cdi52AVBdTlEDW0oVWVMOTlYJWBxHAkM4Ag%3D%3D%7CcSlxBldFFVAYWEsATF4eWEIGUBsvAg%3D%3D%7CcClxBldFFVB7NyV7Iz1gSUkKRRVeAkRVBkpdbEU%3D%7CcylxBldFFVATVV8IUEQQV04LNRo%3D%7CcitzBFUUSngwcGIpb38lDAxPAFAbRwAXRAcTIgs%3D%7CbTRsG0pYX1NRXFdSUhpUDHs5ay5wLGB2TBQCQhQaRRVUDEgPdE4%3D%7CbDRwbDNgPmU5fG44YHUsb28pcy1zNm56MXxoKGtrLmEmYyJlbzp9dypvby1%2BUQ%3D%3D");
		// uaSeeds.add("219fCJmZk4PGRVHHxtOZXsnZHo9ay11PWsQKg%3D%3D%7CfyJ6Zyd9OWImZXchYXUrbB0%3D%7CfiB4D157YHtufDUqfHY4fmo7dCQaAxlbU1AFS2IT%7CeSRiYjNhIHA2cGIycmU9e2UkfT94MXFlNHRnM3JmIXExbShrfDkQ%7CeCVoaEATTRdVGBJEBw1DH0dQQVoaUVMUAVUaBhEAERhIfFE%3D%7Cey93eSgW%7Cei93eSgW%7CdSpyBUAQTBBZFAtVGQtQEA1MHlwMSBAFUQkeRgoQTx9eBUUdClgfD08DGV4RUg5NDwVaHQw9FA%3D%3D%7CdCltbTwERRRWEwVOZmI%2FeG0reTtlJ2R3IWNyYiYkZTF6KmgoPGIlOmIjPXwpal8NVUJ4LlU%3D%7Cdy52AVART306fG4lYH8hCAhLBFQfQwQTQgN4SQ%3D%3D%7Cdi5qdil6JH8jZnQiem82dXUzaTdpLHRgK2ZyMndtQw%3D%3D");
		uaSeeds.add("154fCJmZk4PGRVHHxtOZXsnZHo9ay11PWsQKg%3D%3D%7CfyJ6Zyd9OGAiYnwpaXshYhM%3D%7CfiB4D157YHtufDUqfHY4fmo7dCQaAxlbU1AFS2IT%7CeSRiYjNhIHA3cmY1eW01dW8qczF2NnJjMXxtMXVtL3U%2BZCZkciJiZlc%3D%7CeCVoaEATTRFRFx1MFBABVyYI%7Cey93eSgW%7Cei93eSgW%7CdSpyBUAQThdLDh1LBgxSFAxNAkEdXhwWSQsZWRwFRRBOH1wfZF4%3D%7CdCltbTwERRRWEwVOZmI%2FeG0reTtlJ2R3IWNyYiYkZTF6KmgoPGIlOmIjPXwpal8NVUJ4LlU%3D%7Cdy93AFFDE1YfWUcMQF8fWEEAVnlN%7Cdi93AFFDE1Z9MCVxKTdoQUECTR1WCk1dDUE6Cw%3D%3D%7CcStzBFVHF1IRV10KUkEdUUlnWQ%3D%3D%7CcCpyBVRGFlMSUVsMVEcfUkZoVg%3D%3D%7CcytzBFUUShteHBZADRhYGwFAEj0J%7CcitzBFUUSngwdWcsangtBARHCFgTTwIWRAV%2BTw%3D%3D%7CbTVtGksKVApCAQteHwxMARtZCSYS%7CbDVtGktZXlJQXVZTUxtVDXo4aSt0KGRzSREHRxEfQBJUCEAGfUc%3D%7CbzdzbzBjPWY6f207Y3YvbGwqcC5wNW15Mn9rK2ho");
		uaSeeds.add("139fCJmZk4PGRVHHxtOZXsnZHo9ay11PWsQKg%3D%3D%7CfyJ6Zyd9OGAiYnwva34lZhc%3D%7CfiB4D157YHtufDUqfHY4fmo7dCQaAxlbU1AFS2IT%7CeSRiYjNhIHA3cmY1eWs3cG8lfD55OHRlOn1tOXRrK3Ewby1vfyx6AQ%3D%3D%7CeCVoaEATTRVXGxFOFhJODhZUEVsDWgx3TQ%3D%3D%7Cey93eSgW%7Cei93eSgW%7CdSpyBUAQThdLDh1LBgxSFApIB0QYWxkTTA4cXBkAQBVLGlkaYVs%3D%7CdCltbTwERRRWEwVOZmI%2FeG0reTtlJ2R3IWNyYiYkZTF6KmgoPGIlOmIjPXwpal8NVUJ4LlU%3D%7Cdy93AFFfQkVBXVkSX08RSVYWRxlBAEdQAkA7Cg%3D%3D%7Cdi52AVARTx9cGBJCDx5eGwZGHVkALQQ%3D%7CcSlxBlcWSBlRExlJCRlZHAFGHFsEKQA%3D%7CcClxBlcWSHoyfmkiYXMsBQVGCVkSTgscTgsVSGEQ%7CcypyBVQVS3kxcmwnZHcjCgpJBlYdQQQTQgcUTmcW%7CcitzBFUUSngwc20mZXUqAwNAD18USA0aSgoVS2IT%7CbTVxbTJhP2Q4fW85YXQtbm4ocixyN297MH1pKWpqL2AnYiNkbjh");
		uaSeeds.add("009fCJmZk4PGRVHHxtEb3EtbnA3YSd%2FN2EaIA%3D%3D%7CfyJ6Zyd9OGAiY3UranQoaBk%3D%7CfiB4D15%2BZH9geTp%2FJyN8PDJtLAkJFwdOXlldbEU%3D%7CeSRiYjNhIHA3cmY0cG82dWAkfT94PXFvMXRnMndpI3Ayaih%2BBQ%3D%3D%7CeCVoaEAQTh5bHhRBGR0qJigGOA%3D%3D%7Cey93eSgW%7Cei93eSgW%7CdSpyBUYJSw5PAhZBGQldHR1fBUUcQAUTTAgCXxIMTwBDGl4cZ10%3D%7CdCltbTwEQBpaHQpBaW0wd2IncTJoKGl%2BLWF%2Bbioobjx2KWopP2woPWgrNnAhYFUHX0hyJF8%3D%7Cdy93AFFGW1VCWhlcBBFFAABGHF8aWxcFUXgD%7Cdi52AVBRQkVbSh5bAxRKDhtEFVYHWx4JXRwPPhc%3D%7CcSlxBlcWSBBTEQdMChxIEA1NHloEKQA%3D%7CcClxBlcWSHo7fWo9ZXcoaBlGFUsAUgodSQsfQ2ob%7Ccytvcyx%2FIXomY3Enf2ozcHA2bDJsKXFlLmN3N3R0MX45fD16AQ%3D%3D");
		// uaSeeds.add("047fCJmZk4PGRVHHxtEb3EtbnA3YSd%2FN2EaIA%3D%3D%7CfyJ6Zyd9OGAiY3QjbnEubh8%3D%7CfiB4D15%2BZH9geTp%2FJyN8PDJtLAkJFwdOXlldbEU%3D%7CeSRiYjNhIHA3cmY0cWcyf2EkfT94OXxiPH9oPXpmLX87ZSNufjsS%7CeCVoaEARTxJbAxFaDEcaRk0cIg0%3D%7Cey93eSgW%7Cei93eSgW%7CdSpyBUYJSw5PAxxCGg5QFg5RA0gTUwscSwcRURQBQBNNFVUVBT8W%7CdCltbTwEQBpaHQpBaW0wd2IncTJoKGl%2BLWF%2Bbioobjx2KWopP2woPWgrNnAhYFUHX0hyJF8%3D%7Cdy93AFEQThBVFhxNCB5eGwFBFjkN%7Cdi52AVARTxdXGwhDAxNHHwdFFlIMIQg%3D%7CcSlxBlcWSBheExlKDhhYGAVDGVltQA%3D%3D%7CcClxBldFFVB7Pih5ND5qKlsEVwlCEEhaDkhbDyZX%7CcypyBVRGFlN4PSt6Nz1pLl8AUw1GFExeCkdSCCFQ%7CcitzBFVHF1J5PCp7NjxoL14BUgxHFU1fDEleAClY%7CbTVxbTJhP2Q4fW85YXQtbm4ocixyN297MH1pKWpqL2AnYiNkbjh%2BdClsHQ%3D%3D");
		uaSeeds.add("232fCJmZk4PGRVHHxtEb3EtbnA3YSd%2FN2EaIA%3D%3D%7CfyJ6Zyd9OGAiY3QlYXcjZBU%3D%7CfiB4D15%2BZH9geTp%2FJyN8PDJtLAkJFwdOXlldbEU%3D%7CeSRiYjNhIHA3cmY0cWE9eWInfjx7O3tsO3trNHdoKHo7YClvcSB2DQ%3D%3D%7CeCVoaEASTBRVGQZNCwFPEmMyDCM%3D%7Cey93eSgW%7Cei93eSgW%7CdSpyBUYJSw5PAxxCGg5QFg5RA0gTUwscSwcRURQBQBNNFVUVBT8W%7CdCltbTwEQBpaHQpBaW0wd2IncTJoKGl%2BLWF%2Bbioobjx2KWopP2woPWgrNnAhYFUHX0hyJF8%3D%7Cdy93AFEQTh5WEhhMDBNTFAhNHV4BLAU%3D%7Cdi52AVARTxdXFwRPCh1HHwBDFlMKTmcc%7CcShwB1ZEFFF6Pyl6PDZjJlcIWwVOHERRBkRbA0AxHw%3D%3D%7CcClxBldFFVB7Pih7PTdiJlcIWwVOHERRBkVSDEEwHg%3D%3D%7CcypyBVRGFlN4PSt4PjRhJVQLWAZNH0dSBUZTCEs6FA%3D%3D%7CcihwB1ZEFFEQU1kOVkMfU0wGUn1J%7CbTdvGElbC04MSlUeWlAPSlYdRw05FA%3D%3D%7CbDZuGUhaCk8NS1QfW1EOS1cdTQ46Fw%3D%3D%7CbzdvGElbC04PSVYAWEcYQF8dTQtbHTRP%7CbjZybjFiPGc7fmw6YncubW0rcS9xNGx4M35qKmlpLGMgfSFkcTp%2Fayttcyx9Pns5fXc9eAM%3D");
		uaSeeds.add("003fCJmZk4PGRVHHxtEb3EtbnA3YSd%2FN2EaIA%3D%3D%7CfyJ6Zyd9OGAiY3EhbHovbh8%3D%7CfiB4D15%2BZH9geTp%2FJyN8PDJtLAkJFwdOXlldbEU%3D%7CeSRiYjNhIHA3cmY0dGUwdGAkfT94On1pOXhqN3ZqL381bypodjMa%7CeCVoaEAQThZVGA1GBw1DRRtBWVYdMBk%3D%7Cey93eSgW%7Cei93eSgW%7CdSpyBUYJSw5PAxxCGg5QFg5RA0gTUwscSwcRURQBQBNNFVUVBT8W%7CdCltbTwEQBpaHQpBaW0wd2IncTJoKGl%2BLWF%2Bbioobjx2KWopP2woPWgrNnAhYFUHX0hyJF8%3D%7Cdy93AFEQThZVEw1GARFEHARDElAOIwo%3D%7Cdi52AVARTxdWFwJJDxlBGQFEFlQFKAE%3D%7CcShwB1ZEFFF6Pyh%2FPjRgI1INXgBLGUFTAkNWDyZX%7CcChwB1ZEFFEQVEsfR1gBWUEEXhpGa0I%3D%7CcypyBVQVS3k%2FcmcsaXYsBQVGCVkSTg4fTQEWJw4%3D%7CcihwB1YXSRNRCR9UFApXFg8hHw%3D%3D%7CbTdvGEkIVg5JEQdMDBJJBR8xDw%3D%3D%7CbDZuGUgJVwxOAwleBhRBAB9Va0Q%3D%7CbzZuGUhFVkkbQz5oLjlnPyJiNRpfH0dDBl5NE1FODTMc%7CbjZybjFiPGc7fmw6YncubW0rcS9xNGx4M35qKmlpLGMgfSFhaz17cS9paSh6JH8%2FZ3lD");
		uaSeeds.add("050fCJmZk4PGRVHHxtEb3EtbnA3YSd%2FN2EaIA%3D%3D%7CfyJ6Zyd9OGAiY3AkZXolZxY%3D%7CfiB4D15%2BZH9geTp%2FJyN8PDJtLAkJFwdOXlldbEU%3D%7CeSRiYjNhIHA3cmY0dWA5dGstdDZxNXdgMn5gP35qIHQ0by1oeD0U%7CeCVoaEASTBRWEgNIDwVLUR4CQ2xY%7Cey93eSgW%7Cei93eSgW%7CdSpyBUYJSw5PAxxCGg5QEAlWBE8UVAwbTAAWVhMGRxRKElISAjgR%7CdCltbTwEQBpaHQpBaW0wd2IncTJoKGl%2BLWF%2Bbioobjx2KWopP2woPWgrNnAhYFUHX0hyJF8%3D%7Cdy93AFEQTh5eGRNHBBZWGg9Idlk%3D%7Cdi52AVARTxBXEBpICh1dGwNJE1RgTQ%3D%3D%7CcSlxBlcWSBdfGBJABRJSFA1HHVdjTg%3D%3D%7CcClxBldFFVB7Pih3MTtvLl8AUw1GFExYB0dWAita%7CcylxBldFFVARUlgPV0IcUUkOMB8%3D%7CcihwB1ZEFFETVUoBRU8QV0wJUn1J%7CbTdvGElbC04MSlUeWlAPTVgcSWZS%7CbDVtGktZXlJQXVZTUxtVDXo4by10KGRwShIERBIcQxRTCE8OdU8%3D%7CbzdzbzBjPWY6f207Y3YvbGwqcC5wNW15Mn9rK2hoLWIlYCFmbDl%2BdCpsbC1%2FIXFc");
		uaSeeds.add("204fCJmZk4PGRVHHxtOZXsnZHo9ay11PWsQKg%3D%3D%7CfyJ6Zyd9OGAiY3MnYn0hZBU%3D%7CfiB4D157YHtufDUqfHY4fmo7dCQaAxlbU1AFS2IT%7CeSRiYjNhIHA3cmY0dmM%2BcmktdDZxN3trP3NlOndjI3g4ZiFldDEY%7CeCVoaEARTxdXExlJERVZWBNRDwkEVn8E%7Cey93eSgW%7Cei93eSgW%7CdSpyBUAQThdLDh1LBgxZGwdYCk0XUQkXRgYMURAPSgVPFVJ7AA%3D%3D%7CdCltbTwERRRWEwVOZmI%2FeG0reTtlJ2R3IWNyYiYkZTF6KmgoPGIlOmIjPXwpal8NVUJ4LlU%3D%7Cdy93AFEQTh5ZGBJEBxRUEQ5FFzgM%7Cdi52AVARTx5dHxVLBw1QHAZMcl0%3D%7CcShwB1ZEFFF6NiJ8JDplTEwPQBBbB0FXCUgzAg%3D%3D%7CcChwB1YXSRdXFR9PDgRaFwNBf1A%3D%7CcylxBldFFVAYQFYdWk0SVCUL%7CcitzBFVHQExOQ0hNTQVLE2QmdzVtMXJiWAAWVgAOUQdMFVx1Dg%3D%3D%7CbTVxbTJhP2Q4fW85YXQtbm4ocixyN297MH1pKWpqL2AjfiJiaD16cC1oGQ%3D%3D");
		uaSeeds.add("135fCJmZk4PGRVHHxtOZXsnZHo9ay11PWsQKg%3D%3D%7CfyJ6Zyd9OGAiYHAjbnEoahs%3D%7CfiB4D157YHtufDUqfHY4fmo7dCQaAxlbU1AFS2IT%7CeSRiYjNhIHA3cmY3dWcyf2Yjejh%2FO3hqO3tvO3djJXI0bS5rfi95Ag%3D%3D%7CeCVoaEAQThZVGAZNCgBOR0lnWQ%3D%3D%7Cey93eSgW%7Cei93eSgW%7CdSpyBUAQThdLDh1LBgxRFw5PAEMfXB4USwkbWx4HRxJMHV4dZlw%3D%7CdCltbTwERRRWEwVOZmI%2FeG0reTtlJ2R3IWNyYiYkZTF6KmgoPGIlOmIjPXwpal8NVUJ4LlU%3D%7Cdy93AFEQTh9YFR9LCB9fGABHFzgM%7Cdi93AFEQTnw0eW4lYnMudmIkfyN%2BPnpsOXlrNnZqL345fD9%2BYClvcCVofDhsJ3w1d2k5eHIoaGgofVIXVw8LThYDWRgGKBY%3D%7CcSltcS59I3gkYXMlfWgxcnI0bjBuK3NnLGF1NXZ2NGJN");
		uaSeeds.add("177fCJmZk4PGRVHHxtEb3EtbnA3YSd%2FN2EaIA%3D%3D%7CfyJ6Zyd9OGAiYH4paH4kZhc%3D%7CfiB4D15%2BZH9geTp%2FJyN8PDJtLAkJFwdOXlldbEU%3D%7CeSRiYjNhIHA3cmY3e201eGwocTN0MXNjNXRlOnxoI3k9ZiRkdzIb%7CeCVoaEATTRBMCRhTBVdSSQtYEw4PCAwDHwJCBgtFFCoF%7Cey93eSgW%7Cei93eSgW%7CdSpyBUYJSw5PAxxCGg9RHQRbCUIZWQEWQQ0bWx4LShlHH18fDzUc%7CdCltbTwEQBpaHQpBaW0wd2IncTJoKGl%2BLWF%2Bbioobjx2KWopP2woPWgrNnAhYFUHX0hyJF8%3D%7Cdy93AFFcT1ACWk0bXkkJTFMRXh9DB0paYEk%3D%7Cdi93AFFDE1Z9OC5xMDpuKVgHVApBE0teDEBWCSBR%7CcSlxBldFFVAZVUMIRFAQV00OWx0pBA%3D%3D%7CcCpyBVRGFlMbQ1UeWUcbW0JsUg%3D%3D%7CcylxBldFFVAYVV8IUEURXEQHORY%3D%7CcihwB1ZEFFEWUVsMVEEUUE0MMh0%3D%7CbTRsG0oLVWcubXw3cmY6ExNQH08EWBgPWRUGNx4%3D%7CbDRsG0oLVQ1NCxxXEgVQCBBSBkMcMRg%3D%7CbzZuGUhaXVFTXlVQUBhWDnk7bC5xLWF1TxcBQRcZRhFRAUMHfEY%3D%7CbjZybjFiPGc7fmw6YncubW0rcS9xNGx4M35qKmlpK30jfyNldzx6by9pdyh6OH09ZXtB");
		scope = initScriptable();
	}

	private static ScriptableObject initScriptable() {
		Context cx = Context.enter();
		ScriptableObject scope = cx.initStandardObjects();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		Document doc = docBuilder.newDocument();
		ScriptableObject.putProperty(scope, "$document", doc);
		try {
			InputStream in = null;
			Reader reader = null;
			String optName = "com/yihaodian/pis/javascriptcrawler/resources/taobao.opt.js";
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(optName);
			reader = new InputStreamReader(in);
			cx.evaluateReader(scope, reader, "tbua", 0, null);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);

			String uaName = "com/yihaodian/pis/javascriptcrawler/resources/taobao.ua.js";
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(uaName);
			reader = new InputStreamReader(in);
			cx.evaluateReader(scope, reader, "tbua", 0, null);
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Context.exit();
		return scope;
	}

	public static String selectOne() {
		Random random = new Random();
		int index = random.nextInt(uaSeeds.size());
		int start = random.nextInt(999) + 1;
		String uaHead = "000" + start;
		int numLen = 3;
		uaHead = uaHead.substring(uaHead.length() - numLen);
		String ua = uaSeeds.get(index);
		// ua = uaHead + ua.substring(3);
		return ua;
	}

	public static String newOne() {
		String ua = null;
		Object rs = null;
		try {
			Context cx = Context.enter();
			String source = "ua = '';UA_Opt.Token=new Date().getTime()+':'+Math.random();";
			source += "UA_Opt.oCustom={};UA_Opt.oCustom.oEnv=[400];UA_Opt.oCustom.oSet= [ 20, [ \"de\", \"8_807245418\"] ];UA_Opt.reload();";
			cx.evaluateString(scope, source, "cmd", 0, null);
			
			source = "UA_Opt.oCustom={};UA_Opt.oCustom.oEnv=[400,399];var rand =Math.ceil(Math.random()*(96659-15807)+15807);UA_Opt.oCustom.oSet = [ 20, [ \"to\", [ rand, new Date().getTime(), \"20\" ] ] ];";
			source += "UA_Opt.reload(); var sua = eval(UA_Opt.LogVal);";
			cx.evaluateString(scope, source, "cmd", 0, null);
			rs = ScriptableObject.getProperty(scope, "sua");
			ua = Context.toString(rs);
			Context.exit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ua == null) {
			ua = selectOne();
		}
		return ua;
	}

	@Test
	public void test() throws Exception {
//		System.out.println(newOne());
//		ShuTaobaoClient client = new ShuTaobaoClient();
//		String username = "pis1002";
//		String password = "pis1234";
//		boolean bHadLogin = client.login(username, password);
//		System.out.println(bHadLogin);
//		if (!bHadLogin) {
//			return;
//		}
//		// while(!client.login(username,password)){
//		// TimeUnit.MILLISECONDS.sleep(60000);
//		// System.out.println("retry login:"+(++retry));
//		// }
//		// String ua = newOne();
//		// System.out.println(ua);
//		String url = "http://shu.taobao.com/trendindex?query=%E8%8B%B9%E6%9E%9C";
//		String proxyHost = "10.19.21.208";
//		int proxyPort = 8901;
//		Map<String, String> config = new HashMap<String, String>();
//		config.put("proxyHost", proxyHost);
//		config.put("proxyPort", "" + proxyPort);
//		config = null;
//		String html = SimpleHttpClient.getCurrent().get(url, config, null).getResponseText();
//		System.out.println(html);
	}
}
