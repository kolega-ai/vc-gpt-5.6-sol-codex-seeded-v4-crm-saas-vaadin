package com.northstar.crm.service;
import org.springframework.stereotype.Component;import java.util.Random;
@Component public class AssetVersionService {private final Random random=new Random();/** java.util.Random is intentionally non-cryptographic: this suffix only busts static-asset caches. */public String cacheBustingSuffix(){return Integer.toUnsignedString(random.nextInt(),36);}}
