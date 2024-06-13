import re
import sys

commands_data = {
    'xsaveopt': {'0_0_0': 74.0},
    'xrstor': {'0_0_0': 122.0},
    'xsave': {'0_0_0': 107.0},
    'fxrstor': {'0_0_0': 76.5},
    'fxsave': {'0_0_0': 71.0},
    'stmxcsr': {'0_0_0': 2.0},
    'ldmxcsr': {'0_0_0': 3.0},
    'vzeroall': {'0_0_0': 12.0},
    'vzeroupper': {'0_0_0': 1.0},
    'xorpd': {'0_0_m': 0.5, '0_0_0': 0.33, '0_m_0': 0.5},
    'xorps': {'0_0_m': 0.5, '0_0_0': 0.33, '0_m_0': 0.5},
    'orpd': {'0_0_m': 0.5, '0_0_0': 0.33, '0_m_0': 0.5},
    'orps': {'0_0_m': 0.5, '0_0_0': 0.33, '0_m_0': 0.5},
    'andnpd': {'0_0_m': 0.5, '0_0_0': 0.33, '0_m_0': 0.5},
    'andnps': {'0_0_m': 0.5, '0_0_0': 0.33, '0_m_0': 0.5},
    'andpd': {'0_0_m': 0.5, '0_0_0': 0.33, '0_m_0': 0.5},
    'andps': {'0_0_m': 0.5, '0_0_0': 0.33, '0_m_0': 0.5},
    'rsqrtps': {'0_0_0': 1.0},
    'rsqrtss': {'0_0_0': 1.0},
    'vsqrtpd': {'0_0_0': 5.0},
    'sqrtpd': {'0_0_0': 5.0},
    'sqrtsd': {'0_0_0': 5.0},
    'vsqrtps': {'0_0_0': 6.0},
    'sqrtps': {'0_0_0': 3.0},
    'sqrtss': {'0_0_0': 3.0},
    'vfnmsub': {'0_0_0': 0.5},
    'vfmsub': {'0_0_0': 0.5},
    'vfnmadd': {'0_0_0': 0.5},
    'vfmadd': {'0_0_0': 0.5},
    'dppd': {'0_0_0': 1.0},
    'dpps': {'0_0_0': 1.5},
    'roundpd': {'0_0_0': 1.0},
    'roundps': {'0_0_0': 1.0},
    'roundsd': {'0_0_0': 1.0},
    'roundss': {'0_0_0': 1.0},
    'minpd': {'0_0_0': 0.5},
    'minps': {'0_0_0': 0.5},
    'minsd': {'0_0_0': 0.5},
    'minss': {'0_0_0': 0.5},
    'maxpd': {'0_0_0': 0.5},
    'maxps': {'0_0_0': 0.5},
    'maxsd': {'0_0_0': 0.5},
    'maxss': {'0_0_0': 0.5},
    'ucomisd': {'0_0_0': 1.0},
    'ucomiss': {'0_0_0': 1.0},
    'comisd': {'0_0_0': 1.0},
    'comiss': {'0_0_0': 1.0},
    'CMPccPD': {'0_0_0': 0.5},
    'CMPccPS': {'0_0_0': 0.5},
    'CMPccSD': {'0_0_0': 0.5},
    'CMPccSS': {'0_0_0': 0.5},
    'rcpsps': {'0_0_0': 1.0},
    'rcpps': {'0_0_0': 1.0},
    'rcpss': {'0_0_0': 1.0},
    'vdivpd': {'0_0_0': 8.0},
    'vdivps': {'0_0_0': 5.0},
    'divpd': {'0_0_0': 4.0},
    'divsd': {'0_0_0': 4.0},
    'divps': {'0_0_0': 3.0},
    'divss': {'0_0_0': 3.0},
    'mulpd': {'0_0_0': 0.5},
    'mulps': {'0_0_0': 0.5},
    'mulsd': {'0_0_0': 0.5},
    'mulss': {'0_0_0': 0.5},
    'hsubpd': {'0_0_0': 2.0},
    'hsubps': {'0_0_0': 2.0},
    'haddpd': {'0_0_0': 2.0},
    'haddps': {'0_0_0': 2.0},
    'addsubpd': {'0_0_0': 0.5},
    'addsubps': {'0_0_0': 0.5},
    'subpd': {'0_0_0': 0.5},
    'subps': {'0_0_0': 0.5},
    'subsd': {'0_0_0': 0.5},
    'subss': {'0_0_0': 0.5},
    'addpd': {'0_0_0': 0.5},
    'addps': {'0_0_0': 0.5},
    'addsd': {'0_0_0': 0.5},
    'addss': {'0_0_0': 0.5},
    'vcvtph2ps': {'0_0_0': 1.0},
    'vcvtps2ph': {'0_0_0': 1.0},
    'cvttsd2si': {'0_0_0': 1.0},
    'cvtsd2si': {'0_0_0': 1.0},
    'cvtsi2sd': {'0_0_0': 2.0},
    'cvttss2si': {'0_0_0': 1.0},
    'cvtss2si': {'0_0_0': 1.0},
    'cvtsi2ss': {'0_m_0': 3.0, '0_0_0': 2.0},
    'cvttpd2pi': {'0_0_0': 1.0},
    'cvtpd2pi': {'0_0_0': 1.0},
    'cvtpi2pd': {'0_m_0': 0.5, '0_0_0': 1.0},
    'cvttps2pi': {'0_0_0': 1.0},
    'cvtps2pi': {'0_0_0': 1.0},
    'cvtpi2ps': {'0_0_0': 2.5},
    'vcvttpd2dq': {'0_0_0': 1.0},
    'vcvtpd2dq': {'0_0_0': 1.0},
    'cvttpd2dq': {'0_0_0': 1.0},
    'cvtpd2dq': {'0_0_0': 1.0},
    'vcvtdq2pd': {'0_m_0': 0.5, '0_0_0': 1.0},
    'cvtdq2pd': {'0_0_0': 1.0, '0_m_0': 0.5},
    'vcvtps2dq': {'0_0_0': 0.5},
    'cvttps2dq': {'0_0_0': 0.5},
    'cvtps2dq': {'0_0_0': 0.5},
    'vcvtdq2ps': {'0_0_0': 0.5},
    'cvtdq2ps': {'0_0_0': 0.5},
    'cvtss2sd': {'0_0_0': 2.0},
    'vcvtps2pd': {'0_m_0': 0.5, '0_0_0': 1.0},
    'cvtps2pd': {'0_m_0': 0.5, '0_0_0': 1.0},
    'cvtsd2ss': {'0_0_0': 1.0},
    'vcvtpd2ps': {'0_0_0': 1.0},
    'cvtpd2ps': {'0_0_0': 1.0},
    'vgatherqpd': {'0_0_0': 3.0},
    'vgatherdpd': {'0_0_0': 3.0},
    'vgatherqps': {'0_0_0': 3.0},
    'vgatherdps': {'0_0_0': 4.5},
    'vmaskmovpd': {'0_0_0': 1.0, '0_0_m': 0.5},
    'vmaskmovps': {'0_0_0': 1.0, '0_0_m': 0.5},
    'vinsertf128': {'0_0_0': 1.0, '0_0_m': 0.5},
    'insertps': {'0_0_0': 1.0},
    'vextractf128': {'0_0_0': 1.0},
    'extractps': {'0_0_0': 1.0},
    'unpckd': {'0_0_0': 1.0},
    'unpcklps': {'0_0_0': 1.0},
    'unpckh': {'0_0_0': 1.0},
    'movshup': {'0_m_0': 0.5, '0_0_0': 1.0},
    'movldup': {'0_m_0': 0.5, '0_0_0': 1.0},
    'movsh': {'0_m_0': 0.5, '0_0_0': 1.0},
    'vbroadcastf128': {'0_0_0': 0.5},
    'vbroadcastsd': {'0_0_0': 1.0, '0_m_0': 0.5},
    'vbroadcastss': {'0_0_0': 1.0, '0_m_0': 0.5},
    'movddup': {'0_m_0': 0.5, '0_0_0': 1.0},
    'vblendvpd': {'0_0_0': 1.0},
    'vblendvps': {'0_0_0': 1.0},
    'blendvpd': {'0_0_0': 1.0},
    'blendvps': {'0_0_0': 1.0},
    'blendpd': {'0_m_0': 0.5, '0_0_m': 0.5, '0_0_0': 0.33},
    'blendps': {'0_m_0': 0.5, '0_0_m': 0.5, '0_0_0': 0.33},
    'vpermpd': {'0_0_0': 1.0},
    'vpermps': {'0_0_0': 1.0},
    'vperm2f128': {'0_0_0': 1.0},
    'vpermilpd': {'0_0_0': 1.0},
    'vpermilps': {'0_0_0': 1.0},
    'shufpd': {'0_0_0': 1.0},
    'shufps': {'0_0_0': 1.0},
    'vmovntpd': {'0_0_0': 1.0},
    'vmovntps': {'0_0_0': 1.0},
    'movntpd': {'0_0_0': 1.0},
    'movntps': {'0_0_0': 1.0},
    'vmovmskpd': {'0_0_0': 1.0},
    'vmovmskps': {'0_0_0': 1.0},
    'movmskpd': {'0_0_0': 1.0},
    'movmskps': {'0_0_0': 1.0},
    'movhlps': {'0_0_0': 1.0},
    'movlpd': {'0_0_0': 1.0},
    'movlps': {'0_0_0': 1.0},
    'movhpd': {'0_0_0': 1.0},
    'movhps': {'0_0_0': 1.0},
    'movsd': {'m_0_0': 1.0, '0_m_0': 0.5, '0_0_0': 1.0},
    'movss': {'m_0_0': 1.0, '0_m_0': 0.5, '0_0_0': 1.0},
    'vmovupd': {'m_0_0': 1.0, '0_m_0': 0.5},
    'vmovups': {'m_0_0': 1.0, '0_m_0': 0.5},
    'movupd': {'m_0_0': 1.0, '0_m_0': 0.5},
    'movups': {'m_0_0': 1.0, '0_m_0': 0.5},
    'vmovapd': {'m_0_0': 1.0, '0_m_0': 0.5, '0_0_0': 0.25},
    'vmovaps': {'m_0_0': 1.0, '0_m_0': 0.5, '0_0_0': 0.25},
    'movapd': {'m_0_0': 1.0, '0_m_0': 0.5, '0_0_0': 0.25},
    'movaps': {'m_0_0': 1.0, '0_m_0': 0.5, '0_0_0': 0.25},
    'emms': {'0_0_0': 6.0},
    'aeskeygenassist': {'0_m_0': 12.0, '0_0_0': 12.0},
    'aesimc': {'0_0_0': 2.0},
    'aesdenclast': {'0_m_0': 1.5, '0_0_0': 1.0},
    'aesdenc': {'0_m_0': 1.5, '0_0_0': 1.0},
    'aesdeclast': {'0_m_0': 1.5, '0_0_0': 1.0},
    'aesdec': {'0_m_0': 1.5, '0_0_0': 1.0},
    'pclmulqdq': {'0_0_0': 1.0},
    'pcmpistrm': {'0_0_0': 3.0},
    'pcmpistri': {'0_0_0': 3.0},
    'pcmpestrm': {'0_0_0': 5.0},
    'pcmpestri': {'0_0_0': 4.0},
    'psrldq': {'0_0_0': 1.0},
    'pslldq': {'0_0_0': 1.0},
    'vpsrlvq': {'0_0_0': 0.5},
    'vpsrlvd': {'0_0_0': 0.5},
    'vpsravd': {'0_0_0': 0.5},
    'vpsllvq': {'0_0_0': 0.5},
    'vpsllvd': {'0_0_0': 0.5},
    'psrad': {'0_0_0': 1.0},
    'psraq': {'0_0_0': 1.0},
    'psraw': {'0_0_0': 1.0},
    'psrlq': {'0_0_0': 1.0},
    'psrld': {'0_0_0': 1.0},
    'psrlw': {'0_0_0': 1.0},
    'psllq': {'0_0_0': 1.0},
    'pslld': {'0_0_0': 1.0},
    'psllw': {'0_0_0': 1.0},
    'ptest': {'0_0_0': 1.0},
    'pxor': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'por': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'vpor': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'pandn': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'pand': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'vpxor': {'0_0_0': 0.33, '0_0_m': 0.5, '0_m_0': 0.5},
    'psradq': {'0_0_0': 1.0},
    'psraqq': {'0_0_0': 1.0},
    'psrawq': {'0_0_0': 1.0},
    'psrlqq': {'0_0_0': 1.0},
    'psrlwq': {'0_0_0': 1.0},
    'psllqq': {'0_0_0': 1.0},
    'psllwq': {'0_0_0': 1.0},
    'ptestq': {'0_0_0': 1.0},
    'pxorq': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'porq': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'vporq': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'pandnq': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'pandq': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'mpsadbw': {'0_0_0': 2.0},
    'psadbw': {'0_0_0': 1.0},
    'psignd': {'0_0_0': 0.5},
    'psignw': {'0_0_0': 0.5},
    'psignb': {'0_0_0': 0.5},
    'pabsd': {'0_0_0': 0.5},
    'pabsw': {'0_0_0': 0.5},
    'pabsb': {'0_0_0': 0.5},
    'phminposuw': {'0_0_0': 1.0},
    'pmaxud': {'0_0_0': 0.5},
    'pminud': {'0_0_0': 0.5},
    'pmaxuw': {'0_0_0': 0.5},
    'pminuw': {'0_0_0': 0.5},
    'pmaxub': {'0_0_0': 0.5},
    'pminub': {'0_0_0': 0.5},
    'vpminub': {'0_0_0': 0.5},
    'pmaxsd': {'0_0_0': 0.5},
    'pminsd': {'0_0_0': 0.5},
    'pmaxsw': {'0_0_0': 0.5},
    'pminsw': {'0_0_0': 0.5},
    'pmaxsb': {'0_0_0': 0.5},
    'pminsb': {'0_0_0': 0.5},
    'pmax': {'0_0_0': 0.5},
    'pmin': {'0_0_0': 0.5},
    'pavgw': {'0_0_0': 0.5},
    'pavgb': {'0_0_0': 0.5},
    'pmulhw': {'0_0_0': 1.0},
    'pmaddubsw': {'0_0_0': 0.5},
    'pmaddwd': {'0_0_0': 0.5},
    'pmuludq': {'0_0_0': 0.5},
    'pmuldq': {'0_0_0': 0.5},
    'pmulld': {'0_0_0': 1.0},
    'pmulhrsw': {'0_0_0': 0.5},
    'pmulhuw': {'m_m_0': 1.0, '0_0_0': 0.5},
    'pmullhw': {'m_m_0': 1.0, '0_0_0': 0.5},
    'pmull': {'m_m_0': 1.0, '0_0_0': 0.5},
    'pcmpgtq': {'0_0_0': 1.0},
    'pcmpeqq': {'0_0_0': 0.5},
    'pcmpgtd': {'m_m_0': 1.0, '0_0_0': 0.5},
    'pcmpgtw': {'m_m_0': 1.0, '0_0_0': 0.5},
    'pcmpgtb': {'m_m_0': 1.0, '0_0_0': 0.5},
    'pcmpeqd': {'m_m_0': 1.0, '0_0_0': 0.5},
    'pcmpeqw': {'m_m_0': 1.0, '0_0_0': 0.5},
    'pcmpeqb': {'m_m_0': 1.0, '0_0_0': 0.5},
    'vpcmpeqb': {'m_m_0': 1.0, '0_0_0': 0.5},
    'phsubsd': {'0_0_0': 2.0},
    'phsubd': {'0_0_0': 2.0},
    'phaddsd': {'0_0_0': 2.0},
    'phaddd': {'0_0_0': 2.0},
    'phsubsw': {'0_0_0': 2.0},
    'phsubw': {'0_0_0': 2.0},
    'phaddsw': {'0_0_0': 2.0},
    'phaddw': {'0_0_0': 2.0},
    'phsubs': {'0_0_0': 2.0},
    'phsub': {'0_0_0': 2.0},
    'phadds': {'0_0_0': 2.0},
    'phadd': {'0_0_0': 2.0},
    'psubusq': {'0_m_m': 0.5, '0_0_0': 0.33},
    'paddusq': {'0_m_m': 0.5, '0_0_0': 0.33},
    'psubsq': {'0_m_m': 0.5, '0_0_0': 0.33},
    'paddsq': {'0_m_m': 0.5, '0_0_0': 0.33},
    'psubq': {'0_m_m': 0.5, '0_0_0': 0.33},
    'paddq': {'0_m_m': 0.5, '0_0_0': 0.33},
    'psubusd': {'0_m_m': 0.5, '0_0_0': 0.33},
    'paddusd': {'0_m_m': 0.5, '0_0_0': 0.33},
    'psubsd': {'0_m_m': 0.5, '0_0_0': 0.33},
    'paddsd': {'0_m_m': 0.5, '0_0_0': 0.33},
    'psubd': {'0_m_m': 0.5, '0_0_0': 0.33},
    'paddd': {'0_m_m': 0.5, '0_0_0': 0.33},
    'psubusw': {'0_m_m': 0.5, '0_0_0': 0.33},
    'paddusw': {'0_m_m': 0.5, '0_0_0': 0.33},
    'psubsw': {'0_m_m': 0.5, '0_0_0': 0.33},
    'paddsw': {'0_m_m': 0.5, '0_0_0': 0.33},
    'psubw': {'0_m_m': 0.5, '0_0_0': 0.33},
    'paddw': {'0_m_m': 0.5, '0_0_0': 0.33},
    'psubusb': {'0_m_m': 0.5, '0_0_0': 0.33},
    'paddusb': {'0_m_m': 0.5, '0_0_0': 0.33},
    'psubsb': {'0_m_m': 0.5, '0_0_0': 0.33},
    'paddsb': {'0_m_m': 0.5, '0_0_0': 0.33},
    'psubb': {'0_m_m': 0.5, '0_0_0': 0.33},
    'paddb': {'0_m_m': 0.5, '0_0_0': 0.33},
    'psubus': {'0_m_m': 0.5, '0_0_0': 0.33},
    'paddus': {'0_m_m': 0.5, '0_0_0': 0.33},
    'psubs': {'0_m_m': 0.5, '0_0_0': 0.33},
    'padds': {'0_m_m': 0.5, '0_0_0': 0.33},
    'psub': {'0_m_m': 0.5, '0_0_0': 0.33},
    'padd': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpclmulqdq': {'0_0_0': 1.0},
    'vpcmpistrm': {'0_0_0': 3.0},
    'vpcmpistri': {'0_0_0': 3.0},
    'vpcmpestrm': {'0_0_0': 5.0},
    'vpcmpestri': {'0_0_0': 4.0},
    'vpsrldq': {'0_0_0': 1.0},
    'vpslldq': {'0_0_0': 1.0},
    'vvpsrlvq': {'0_0_0': 0.5},
    'vvpsrlvd': {'0_0_0': 0.5},
    'vvpsravd': {'0_0_0': 0.5},
    'vvpsllvq': {'0_0_0': 0.5},
    'vvpsllvd': {'0_0_0': 0.5},
    'vpsrad': {'0_0_0': 1.0},
    'vpsraq': {'0_0_0': 1.0},
    'vpsraw': {'0_0_0': 1.0},
    'vpsrlq': {'0_0_0': 1.0},
    'vpsrld': {'0_0_0': 1.0},
    'vpsrlw': {'0_0_0': 1.0},
    'vpsllq': {'0_0_0': 1.0},
    'vpslld': {'0_0_0': 1.0},
    'vpsllw': {'0_0_0': 1.0},
    'vptest': {'0_0_0': 1.0},
    'vvpor': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'vpandn': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'vpand': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'vvpxor': {'0_0_0': 0.33},
    'vpsradq': {'0_0_0': 1.0},
    'vpsraqq': {'0_0_0': 1.0},
    'vpsrawq': {'0_0_0': 1.0},
    'vpsrlqq': {'0_0_0': 1.0},
    'vpsrlwq': {'0_0_0': 1.0},
    'vpsllqq': {'0_0_0': 1.0},
    'vpsllwq': {'0_0_0': 1.0},
    'vptestq': {'0_0_0': 1.0},
    'vpxorq': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'vvporq': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'vpandnq': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'vpandq': {'0_0_m': 0.5, '0_m_0': 0.5, '0_0_0': 0.33},
    'vmpsadbw': {'0_0_0': 2.0},
    'vpsadbw': {'0_0_0': 1.0},
    'vpsignd': {'0_0_0': 0.5},
    'vpsignw': {'0_0_0': 0.5},
    'vpsignb': {'0_0_0': 0.5},
    'vpabsd': {'0_0_0': 0.5},
    'vpabsw': {'0_0_0': 0.5},
    'vpabsb': {'0_0_0': 0.5},
    'vphminposuw': {'0_0_0': 1.0},
    'vpmaxud': {'0_0_0': 0.5},
    'vpminud': {'0_0_0': 0.5},
    'vpmaxuw': {'0_0_0': 0.5},
    'vpminuw': {'0_0_0': 0.5},
    'vpmaxub': {'0_0_0': 0.5},
    'vvpminub': {'0_0_0': 0.5},
    'vpmaxsd': {'0_0_0': 0.5},
    'vpminsd': {'0_0_0': 0.5},
    'vpmaxsw': {'0_0_0': 0.5},
    'vpminsw': {'0_0_0': 0.5},
    'vpmaxsb': {'0_0_0': 0.5},
    'vpminsb': {'0_0_0': 0.5},
    'vpmax': {'0_0_0': 0.5},
    'vpmin': {'0_0_0': 0.5},
    'vpavgw': {'0_0_0': 0.5},
    'vpavgb': {'0_0_0': 0.5},
    'vpmulhw': {'0_0_0': 1.0},
    'vpmaddubsw': {'0_0_0': 0.5},
    'vpmaddwd': {'0_0_0': 0.5},
    'vpmuludq': {'0_0_0': 0.5},
    'vpmuldq': {'0_0_0': 0.5},
    'vpmulld': {'0_0_0': 1.0},
    'vpmulhrsw': {'0_0_0': 0.5},
    'vpmulhuw': {'m_m_0': 1.0, '0_0_0': 0.5},
    'vpmullhw': {'m_m_0': 1.0, '0_0_0': 0.5},
    'vpmull': {'m_m_0': 1.0, '0_0_0': 0.5},
    'vpcmpgtq': {'0_0_0': 1.0},
    'vpcmpeqq': {'0_0_0': 0.5},
    'vpcmpgtd': {'m_m_0': 1.0, '0_0_0': 0.5},
    'vpcmpgtw': {'m_m_0': 1.0, '0_0_0': 0.5},
    'vpcmpgtb': {'m_m_0': 1.0, '0_0_0': 0.5},
    'vpcmpeqd': {'m_m_0': 1.0, '0_0_0': 0.5},
    'vpcmpeqw': {'m_m_0': 1.0, '0_0_0': 0.5},
    'vvpcmpeqb': {'m_m_0': 1.0, '0_0_0': 0.5},
    'vphsubsd': {'0_0_0': 2.0},
    'vphsubd': {'0_0_0': 2.0},
    'vphaddsd': {'0_0_0': 2.0},
    'vphaddd': {'0_0_0': 2.0},
    'vphsubsw': {'0_0_0': 2.0},
    'vphsubw': {'0_0_0': 2.0},
    'vphaddsw': {'0_0_0': 2.0},
    'vphaddw': {'0_0_0': 2.0},
    'vphsubs': {'0_0_0': 2.0},
    'vphsub': {'0_0_0': 2.0},
    'vphadds': {'0_0_0': 2.0},
    'vphadd': {'0_0_0': 2.0},
    'vpsubusq': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpaddusq': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpsubsq': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpaddsq': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpsubq': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpaddq': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpsubusd': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpaddusd': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpsubsd': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpaddsd': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpsubd': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpaddd': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpsubusw': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpaddusw': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpsubsw': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpaddsw': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpsubw': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpaddw': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpsubusb': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpaddusb': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpsubsb': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpaddsb': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpsubb': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpaddb': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpsubus': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpaddus': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpsubs': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpadds': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpsub': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpadd': {'0_m_m': 0.5, '0_0_0': 0.33},
    'vpgatherqq': {'0_0_0': 3.0},
    'vpgatherdq': {'0_0_0': 3.0},
    'vpgatherqd': {'0_0_0': 3.0},
    'vpgatherdd': {'0_0_0': 4.5},
    'vbroadcasti128': {'0_0_0': 0.5},
    'vpbroadcastq': {'0_m_0': 0.5, '0_0_0': 1.0},
    'vpbroadcastd': {'0_m_0': 0.5, '0_0_0': 1.0},
    'vpbroadcastw': {'0_m_0': 1.0, '0_0_0': 1.0},
    'vpbroadcastb': {'0_m_0': 1.0, '0_0_0': 1.0},
    'vinserti128': {'0_0_m': 0.5, '0_0_0': 1.0},
    'pinsrq': {'0_m_0': 1.0, '0_r_0': 2.0},
    'pinsrd': {'0_m_0': 1.0, '0_r_0': 2.0},
    'pinsrw': {'0_m_0': 1.0, '0_r_0': 2.0},
    'pinsrb': {'0_m_0': 1.0, '0_r_0': 2.0},
    'vextracti128': {'0_0_0': 1.0},
    'pextrq': {'0_0_0': 1.0},
    'pextrd': {'0_0_0': 1.0},
    'pextrw': {'0_0_0': 1.0},
    'pextrb': {'0_0_0': 1.0},
    'pmovmskb': {'0_0_0': 1.0},
    'vpmovmskb': {'0_0_0': 1.0},
    'vpmaskmovq': {'m_0_0': 1.0, '0_0_m': 0.5},
    'vpmaskmovd': {'m_0_0': 1.0, '0_0_m': 0.5},
    'maskmovdqu': {'0_0_0': 6.0},
    'maskmovq': {'0_0_0': 2.0},
    'vperm2i128': {'0_0_0': 1.0},
    'vpermq': {'0_0_0': 1.0},
    'vpermd': {'0_0_0': 1.0},
    'vpblendd': {'0_0_m': 0.5, '0_0_0': 0.33},
    'pblendw': {'0_0_0': 1.0},
    'vpblendvb': {'0_0_m': 2.0, '0_0_0': 1.0},
    'pblendvb': {'0_m_0': 2.0, '0_0_0': 1.0},
    'palignr': {'0_0_0': 1.0},
    'pshufhw': {'0_0_0': 1.0},
    'pshuhw': {'0_0_0': 1.0},
    'pshufl': {'0_0_0': 1.0},
    'pshufbd': {'0_0_0': 1.0},
    'pshufw': {'0_0_0': 1.0},
    'pshufb': {'0_0_0': 1.0},
    'vpmovdq': {'0_0_0': 1.0},
    'vpmovdw': {'0_0_0': 1.0},
    'vpmovbq': {'0_0_0': 1.0},
    'vpmovbd': {'0_0_0': 1.0},
    'vpmovbw': {'0_0_0': 1.0},
    'vpmovzx': {'0_0_0': 1.0},
    'vpmovsx': {'0_0_0': 1.0},
    'pmovdq': {'0_0_0': 1.0},
    'pmovdw': {'0_0_0': 1.0},
    'pmovbq': {'0_0_0': 1.0},
    'pmovbd': {'0_0_0': 1.0},
    'pmovbw': {'0_0_0': 1.0},
    'pmovzx': {'0_0_0': 1.0},
    'pmovsx': {'0_0_0': 1.0},
    'punpcklqdq': {'0_0_0': 1.0},
    'punpckhqdq': {'0_0_0': 1.0},
    'punpckdq': {'0_0_0': 1.0},
    'punpckwd': {'0_0_0': 1.0},
    'punpckbw': {'0_0_0': 1.0},
    'punpckl': {'0_0_0': 1.0},
    'punpckh': {'0_0_0': 1.0},
    'packusdw': {'0_0_0': 1.0},
    'packssdw': {'0_0_0': 1.0, 'm_m_0': 2.0},
    'packuswb': {'0_0_0': 1.0, 'm_m_0': 2.0},
    'packsswb': {'0_0_0': 1.0, 'm_m_0': 2.0},
    'vmovntdqa': {'0_0_0': 0.5},
    'movntdqa': {'0_0_0': 0.5},
    'vmovntdq': {'0_0_0': 1.0},
    'movntdq': {'0_0_0': 1.0},
    'movntq': {'0_0_0': 1.0},
    'movq2dq': {'0_0_0': 1.0},
    'movdq2q': {'0_0_0': 1.0},
    'lddqu': {'0_0_0': 0.5},
    'vmovdqu': {'0_0_0': 0.25, 'm_0_0': 1.0, '0_m_0': 0.5},
    'vmovdqa': {'0_0_0': 0.25, 'm_0_0': 1.0, '0_m_0': 0.5},
    'movdqu': {'0_0_0': 0.25, 'm_0_0': 1.0, '0_m_0': 0.5},
    'movdqa': {'0_0_0': 0.25, 'm_0_0': 1.0, '0_m_0': 0.5},
    'movq': {'m_m_0': 0.5, '0_0_0': 1.0},
    'movd': {'r_m_0': 1.0},
    'fninit': {'0_0_0': 78.0},
    'fnclex': {'0_0_0': 22.0},
    'wait': {'0_0_0': 1.0},
    'fnop': {'0_0_0': 0.5},
    'fpatan': {'0_0_0': 130.0},
    'fptan': {'0_0_0': 150.0},
    'fyl2xp1': {'0_0_0': 77.0},
    'fyl2x': {'0_0_0': 103.0},
    'f2xm1': {'0_0_0': 70.0},
    'fsincos': {'0_0_0': 70.0},
    'fcos': {'0_0_0': 63.0},
    'fsin': {'0_0_0': 60.0},
    'fsqrt': {'0_0_0': 5.5},
    'fxtract': {'0_0_0': 11.0},
    'fscale': {'0_0_0': 130.0},
    'frndint': {'0_0_0': 11.0},
    'fprem1': {'0_0_0': 17.0},
    'fprem': {'0_0_0': 17.0},
    'fxam': {'0_0_0': 2.0},
    'ftst': {'0_0_0': 1.0},
    'ficomp': {'0_0_0': 2.0},
    'ficom': {'0_0_0': 2.0},
    'fidivr': {'0_0_0': 4.5},
    'fidiv': {'0_0_0': 4.5},
    'fimul': {'0_0_0': 1.0},
    'fisubr': {'0_0_0': 2.0},
    'fisub': {'0_0_0': 2.0},
    'fiadd': {'0_0_0': 2.0},
    'fucomi': {'0_0_0': 1.0},
    'fcomip': {'0_0_0': 1.0},
    'fcomi': {'0_0_0': 1.0},
    'fucompp': {'0_0_0': 1.0},
    'fcompp': {'0_0_0': 1.0},
    'fucom': {'0_0_0': 1.0},
    'fcomp': {'0_0_0': 1.0},
    'fcom': {'0_0_0': 1.0},
    'fchs': {'0_0_0': 1.0},
    'fabs': {'0_0_0': 1.0},
    'fdivrp': {'0_0_0': 4.5},
    'fdivp': {'0_0_0': 4.5},
    'fdivr': {'0_0_0': 4.5},
    'fdiv': {'0_0_0': 4.5},
    'fmulp': {'0_0_0': 1.0},
    'fmul': {'0_0_0': 1.0},
    'fsubrp': {'0_0_0': 1.0},
    'fsubp': {'0_0_0': 1.0},
    'fsubr': {'0_0_0': 1.0},
    'fsub': {'0_0_0': 1.0},
    'faddp': {'0_0_0': 1.0},
    'fadd': {'0_0_0': 1.0},
    'frstor': {'0_0_0': 175.0},
    'fnsave': {'0_0_0': 176.0},
    'ffreep': {'0_0_0': 0.5},
    'ffree': {'0_0_0': 0.5},
    'fdecstp': {'0_0_0': 0.5},
    'fincstp': {'0_0_0': 0.5},
    'fnstcw': {'0_0_0': 1.0},
    'fldcw': {'0_0_0': 2.0},
    'fnstsw': {'0_0_0': 1.5},
    'FCMOVcc': {'0_0_0': 2.0},
    'fldl2e': {'0_0_0': 2.0},
    'fldpi': {'0_0_0': 2.0},
    'fild1': {'0_0_0': 2.0},
    'fldz': {'0_0_0': 1.0},
    'fisttp': {'0_0_0': 2.0},
    'fistp': {'0_0_0': 1.0},
    'fist': {'0_0_0': 1.0},
    'fild': {'0_0_0': 1.0},
    'fxch': {'0_0_0': 0.5},
    'fbstp': {'0_0_0': 266.0},
    'fstp': {'m_0_0': 1.0, 'r_0_0': 0.5},
    'fst': {'m_0_0': 1.0, 'r_0_0': 0.5},
    'fbld': {'0_0_0': 22.0},
    'fld': {'m_0_0': 0.5, 'r_0_0': 0.5},
    'cpuid': {'0_0_0': 175.0},
    'rdtscp': {'0_0_0': 32.0},
    'rdseed': {'0_0_0': 460.0},
    'rdrand': {'0_0_0': 460.0},
    'rdpmc': {'0_0_0': 40.0},
    'rdtsc': {'0_0_0': 25.0},
    'xgetbv': {'0_0_0': 9.0},
    'leave': {'0_0_0': 5.0},
    'enter': {'0_0_0': 8.0},
    'pause': {'0_0_0': 9.0},
    'longnop': {'0_0_0': 0.25},
    'nop': {'0_0_0': 0.25},
    'lockcmpxchg16b': {'0_0_0': 26.0},
    'cmpxchg16b': {'0_0_0': 16.0},
    'lockcmpxchg8b': {'0_0_0': 19.0},
    'cmpxchg8b': {'0_0_0': 11.0},
    'lockcmpxchg': {'0_0_0': 18.0},
    'cmpxchg': {'0_0_0': 6.0},
    'lockadd': {'0_0_0': 18.0},
    'lockxadd': {'0_0_0': 18.0},
    'xadd': {'0_0_0': 5.0},
    'repcmps': {'0_0_0': 25.0},
    'cmps': {'0_0_0': 4.0},
    'repscas': {'0_0_0': 25.0},
    'scas': {'0_0_0': 1.0},
    'repmovs': {'0_0_0': 10.0},
    'movs': {'0_0_0': 4.0},
    'stos': {'0_0_0': 5.0},
    'repstos': {'0_0_0': 5.0},
    'replods': {'0_0_0': 20.0},
    'lods': {'0_0_0': 20.0},
    'rep': {'0_0_0': 20.0},
    'lodsq': {'0_0_0': 1.0},
    'lodsd': {'0_0_0': 1.0},
    'lodsw': {'0_0_0': 1.0},
    'lodsb': {'0_0_0': 1.0},
    'into': {'0_0_0': 6.0},
    'bound': {'0_0_0': 8.0},
    'ret': {'0_I_0': 2.0, 'I_0_0': 2.0, '0_0_0': 1.0},
    'retq': {'0_I_0': 2.0, 'I_0_0': 2.0, '0_0_0': 1.0},
    'call': {'0_m_0': 3.0, 'm_0_0': 3.0, '0_0_0': 2.0},
    'callq': {'0_m_0': 3.0, 'm_0_0': 3.0, '0_0_0': 2.0},
    'loopne': {'0_0_0': 6.0},
    'loope': {'0_0_0': 6.0},
    'loop': {'0_0_0': 5.0},
    'jrcxz': {'0_0_0': 1.0},
    'jecxz': {'0_0_0': 1.0},
    'jns': {'0_0_0': 1.0},
    'js': {'0_0_0': 1.0},
    'jpo': {'0_0_0': 1.0},
    'jnp': {'0_0_0': 1.0},
    'jpe': {'0_0_0': 1.0},
    'jp': {'0_0_0': 1.0},
    'jo': {'0_0_0': 1.0},
    'jnc': {'0_0_0': 1.0},
    'jc': {'0_0_0': 1.0},
    'jxcz': {'0_0_0': 1.0},
    'jnae': {'0_0_0': 1.0},
    'jbe': {'0_0_0': 1.0},
    'jb': {'0_0_0': 1.0},
    'jnb': {'0_0_0': 1.0},
    'jae': {'0_0_0': 1.0},
    'jnbe': {'0_0_0': 1.0},
    'ja': {'0_0_0': 1.0},
    'jng': {'0_0_0': 1.0},
    'jle': {'0_0_0': 1.0},
    'jnge': {'0_0_0': 1.0},
    'jl': {'0_0_0': 1.0},
    'jnl': {'0_0_0': 1.0},
    'jge': {'0_0_0': 1.0},
    'jnle': {'0_0_0': 1.0},
    'jg': {'0_0_0': 1.0},
    'jnz': {'0_0_0': 1.0},
    'jne': {'0_0_0': 1.0},
    'jz': {'0_0_0': 1.0},
    'je': {'0_0_0': 1.0},
    'jmp': {'0_0_0': 2.0},
    'pext': {'0_0_0': 1.0},
    'pdep': {'0_0_0': 1.0},
    'bzhi': {'0_0_0': 0.5},
    'bextr': {'r_m_r': 1.0, 'r_r_r': 0.5},
    'blsr': {'0_0_0': 0.5},
    'blsmsk': {'0_0_0': 0.5},
    'blsi': {'0_0_0': 0.5},
    'andn': {'0_0_0': 0.5},
    'tzcnt': {'0_0_0': 1.0},
    'lzcnt': {'0_0_0': 1.0},
    'std': {'0_0_0': 4.0},
    'cld': {'0_0_0': 4.0},
    'stc': {'0_0_0': 0.25},
    'cmc': {'0_0_0': 1.0},
    'clc': {'0_0_0': 0.25},
    'SETcc': {'m_0_0': 1.0, 'r_0_0': 0.5},
    'set': {'m_0_0': 1.0, 'r_0_0': 0.5, '0_0_0': 0.8},
    'bsr': {'0_0_0': 1.0},
    'bsf': {'0_0_0': 1.0},
    'btc': {'m_r_0': 5.0, 'm_I_0': 1.0, 'r_0_0': 0.5},
    'bts': {'m_r_0': 5.0, 'm_I_0': 1.0, 'r_0_0': 0.5},
    'btr': {'m_r_0': 5.0, 'm_I_0': 1.0, 'r_0_0': 0.5},
    'bt': {'m_r_0': 5.0, 'm_I_0': 0.5, 'r_0_0': 0.5},
    'ror': {'0_0_0': 0.5, 'm_cl_0': 4.0, 'r_cl_0': 2.0, 'm_I_0': 2.0, 'r_I_0': 0.5},
    'sar': {'0_0_0': 0.5, 'm_cl_0': 4.0, 'r_cl_0': 2.0, 'm_I_0': 2.0, 'r_I_0': 0.5},
    'shr': {'0_0_0': 0.5, 'm_r_cl': 4.0, 'r_r_cl': 2.0, 'm_r_I': 2.0, 'r_r_I': 1.0, 'm_cl_0': 4.0, 'r_cl_0': 2.0, 'm_I_0': 2.0, 'r_I_0': 0.5},
    'shl': {'0_0_0': 0.5, 'm_r_cl': 4.0, 'r_r_cl': 2.0, 'm_r_I': 2.0, 'r_r_I': 1.0, 'm_cl_0': 4.0, 'r_cl_0': 2.0, 'm_I_0': 2.0, 'r_I_0': 0.5},
    'rorx': {'0_0_0': 0.5},
    'sarx': {'0_0_0': 0.5},
    'shrx': {'0_0_0': 0.5},
    'shlx': {'0_0_0': 0.5},
    'shld': {'m_r_cl': 4.0, 'r_r_cl': 2.0, 'm_r_I': 2.0, 'r_r_I': 1.0},
    'shrd': {'m_r_cl': 4.0, 'r_r_cl': 2.0, 'm_r_I': 2.0, 'r_r_I': 1.0},
    'rcl': {'0_0_0': 6.0},
    'rcr': {'0_0_0': 6.0},
    'rol': {'m_cl_0': 4.0, 'r_cl_0': 2.0, 'm_I_0': 2.0, 'r_I_0': 0.5},
    'test': {'m_0_0': 0.5, 'r_0_0': 0.25},
    'xor': {'m_0_0': 1.0, 'r_m_0': 0.5, 'r_r_0': 0.25, 'r_I_0': 0.25},
    'or': {'m_0_0': 1.0, 'r_m_0': 0.5, 'r_r_0': 0.25, 'r_I_0': 0.25},
    'and': {'m_0_0': 1.0, 'r_m_0': 0.5, 'r_r_0': 0.25, 'r_I_0': 0.25},
    'crc32': {'0_0_0': 1.0},
    'popcnt': {'0_0_0': 1.0},
    'cqo': {'0_0_0': 1.0},
    'cdq': {'0_0_0': 1.0},
    'cwde': {'0_0_0': 1.0},
    'cdqe': {'0_0_0': 1.0},
    'cbw': {'0_0_0': 1.0},
    'idiv': {'0_0_0': 45.0},
    'div': {'0_0_0': 35.0},
    'mulx': {'0_0_0': 1.0},
    'imul': {'0_0_0': 1.0},
    'mul': {'0_0_0': 1.0},
    'aam': {'0_0_0': 7.0},
    'not': {'m_0_0': 1.0, 'r_0_0': 0.25},
    'neg': {'m_0_0': 1.0, 'r_0_0': 0.25},
    'dec': {'m_0_0': 1.0, 'r_0_0': 0.25},
    'inc': {'m_0_0': 1.0, 'r_0_0': 0.25},
    'cmp': {'m_0_0': 0.5, 'r_0_0': 0.25},
    'sbb': {'m_0_0': 2.0, 'r_m_0': 1.0, 'r_I_0': 1.0, 'r_r_0': 1.0},
    'adc': {'m_0_0': 2.0, 'r_m_0': 1.0, 'r_I_0': 1.0, 'r_r_0': 1.0},
    'sub': {'m_0_0': 1.0, 'r_m_0': 0.5, 'r_I_0': 0.25, 'r_r_0': 0.25},
    'add': {'m_0_0': 1.0, 'r_m_0': 0.5, 'r_I_0': 0.25, 'r_r_0': 0.25},
    'sfence': {'0_0_0': 6.0},
    'mfence': {'0_0_0': 33.0},
    'lfence': {'0_0_0': 4.0},
    'prefetchw': {'0_0_0': 1.0},
    'prefetchnta2': {'0_0_0': 0.5},
    'prefetchnta1': {'0_0_0': 0.5},
    'prefetchnta0': {'0_0_0': 0.5},
    'prefetchnta': {'0_0_0': 0.5},
    'movbe': {'m_r_0': 1.0, 'r_m_0': 0.6},
    'bswap': {'0_0_0': 1.0},
    'lea': {'0_0_0': 1.0},
    'sahf': {'0_0_0': 1.0},
    'lahf': {'0_0_0': 1.0},
    'popad': {'0_0_0': 8.0},
    'popfq': {'0_0_0': 20.0},
    'popfd': {'0_0_0': 20.0},
    'popa': {'0_0_0': 8.0},
    'popf': {'0_0_0': 20.0},
    'pop': {'m_0_0': 1.0, 'sp_0_0': 3.0, 'r_0_0': 0.5},
    'popq': {'m_0_0': 1.0, 'sp_0_0': 3.0, 'r_0_0': 0.5},
    'pushad': {'0_0_0': 8.0},
    'pushfq': {'0_0_0': 1.0},
    'pushfd': {'0_0_0': 1.0},
    'pusha': {'0_0_0': 8.0},
    'pushhf': {'0_0_0': 1.0},
    'push': {'0_0_0': 1.0},
    'xlat': {'0_0_0': 2.0},
    'xchg': {'0_0_0': 1.0},
    'CMOVcc': {'r_m_0': 0.5, 'r_r_0': 0.5},
    'cmov': {'r_m_0': 0.5, 'r_r_0': 0.5, '0_0_0': 0.5},
    'movzx': {'r_m_0': 0.5, 'r_r_0': 0.25},
    'movsxd': {'r_m_0': 0.5, 'r_r_0': 0.25},
    'movsx': {'r_m_0': 0.5, 'r_r_0': 0.25},
    'movnti': {'0_0_0': 1.0},
    'mov': {'m_0_0': 1.0, 'r_m_0': 0.5, 'r_r_0': 0.25, 'r_I_0': 0.25},
    'vmov': {'m_0_0': 1.0, 'r_m_0': 0.5, 'r_r_0': 0.25, 'r_I_0': 0.25},
    'movabs': {'m_0_0': 1.0, 'r_m_0': 0.5, 'r_r_0': 0.25, 'r_I_0': 0.25},
    'addr32': {'0_0_0': 0.25},
    'data16': {'0_0_0': 0.25},
    'endbr64': {'0_0_0': 0.75},
    'endbr32': {'0_0_0': 0.75},
    'movb': {'0_0_0': 1.0},
    'movl': {'0_0_0': 1.0},
    'movzbl': {'0_0_0': 1.0},


}


def find_closest_match(data, input_command, retries=0):
    base_command, *input_subparts = input_command.split('_')
    input_sub_key = '_'.join(input_subparts)
    
    if base_command in data:
        sub_commands = data[base_command]
        # Sort keys to prioritize matches with more initial matching components
        sorted_sub_commands = sorted(sub_commands.keys(), key=lambda x: -sum(1 for a, b in zip(x.split('_'), input_sub_key.split('_')) if a == b))
        
        best_match = None
        best_match_score = -1

        for sub_command in sorted_sub_commands:
            # Calculate score based on number of matching subparts
            score = sum(a == b for a, b in zip(sub_command.split('_'), input_sub_key.split('_')))
            if score > best_match_score:
                best_match_score = score
                best_match = sub_command
        
        if best_match:
            #print (f"{base_command}_{best_match} {sub_commands[best_match]}")
            return sub_commands[best_match]
    if len(base_command) > 3 and retries<4:
        # Remove the last character
        base_command = base_command[:-1] 
        base_command = base_command +"_"+ input_sub_key
        return find_closest_match(data, base_command,retries+1)
    print(f"No close match found for {input_command}")
    return 6.0



def process_instruction(operator, operands):
    # Step 1: Remove whitespaces from the operator
    operator = operator.replace(' ', '')

    # Step 2: Process each operand
    processed_operands = []
    for operand in operands:
        if operand == '%cl':
            processed_operands.append('cl')
        elif operand == '%sp':
            processed_operands.append('sp')
        elif operand.startswith('%'):
            processed_operands.append('r')
        elif operand.startswith('$') or '%' not in operand:
            processed_operands.append('I')
        else:
            processed_operands.append('m')

    # Step 3: Ensure exactly three operands
    while len(processed_operands) < 3:
        processed_operands.append('0')

    # Step 4: Combine into the final format
    result = f"{operator}_{processed_operands[0]}_{processed_operands[1]}_{processed_operands[2]}"
    return result

def process_assembly_line(line):
    # Strip whitespace and ignore lines not starting with a number followed by a colon
    line = line.strip()
    if not re.match(r'^\d+\.\d+ :', line):
        return None  # Skip non-matching lines

    # Remove comments and extract the number and assembly instruction
    line = re.split(r'#', line)[0].strip()
    parts = re.split(r' : ', line, maxsplit=1)
    if len(parts) < 2:
        return None  # If line does not split into at least two parts, ignore it

    percentage = float(parts[0])
    #if the percentage is 0 then return 0
    if percentage == 0:
        return 0
    instruction = parts[1]

    # Remove address and colon following it to isolate the command
    command_parts = re.split(r':', instruction, maxsplit=1)
    if len(command_parts) < 2:
        return None  # If line does not split into at least two parts, ignore it

    command = command_parts[1].strip()

    # Split the command into operation and operands
    operation_parts = re.split(r'\s+', command, maxsplit=1)
    operation = operation_parts[0].strip()
    operands = operation_parts[1].split()[0] if len(operation_parts) > 1 else None

    # Further split operands if present
    if operands:
        operands = re.split(r',', operands)
        operands = [op.strip() for op in operands]
    else:
        operands = []

    # Return the extracted data as a tuple
    #print(percentage, operation, operands)
    instr= process_instruction(operation, operands)
    #print(instr)
    weight = find_closest_match(commands_data, instr)
    #print(weight)
    return weight

def read_and_process_file(filename):
    with open(filename, 'r') as file:
        sections = {}
        current_section = None
        current_weight = 0
        collecting = False

        for line in file:
            line = line.strip()
            if 'Disassembly of section' in line:
                if current_section is not None:
                    sections[current_section] = current_weight
                collecting = True
                current_weight = 0
                continue
            if collecting:
                if line == '':
                    continue  # Skip the empty line right after section start
                if '<' in line and line.endswith('>:'):
                    # This line contains the section identifier
                    current_section_dirty = line.split('<')[1].split('>')[0]
                    current_section = re.sub(r'^[_@]+', '', current_section_dirty)

                    continue
                if 'Percent |' in line:
                    sections[current_section] = current_weight
                    collecting = False
                    current_section = None
                    continue
                # Process the line to extract the weight, assuming it starts with a float
                #if the line starts with a number followed by a colon, then it is a weight
                if re.match(r'^\d+\.\d+ :', line):
                    weight = float(line.split(':')[0].strip())
                    current_weight += weight*process_assembly_line(line)
                    
                else:
                    # Ignore lines that do not start with a number followed by a colon
                    continue
                
        # In case the last section does not end with a 'Percent |' line
        if current_section and collecting:
            sections[current_section] = current_weight

    return sections


def process_file(file_path):
    # Regular expression to match the lines with the specified pattern
    # This regex captures lines that start with spaces, followed by two percentages, and ends with capturing the last word before a whitespace
    pattern = re.compile(r"^\s+(\d+\.\d+)%\s+(\d+\.\d+)%.*\s+(\S+)$")

    # Dictionary to store the results
    results = {}
    # Open and read the file
    with open(file_path, 'r') as file:
        for line in file:
            match = pattern.search(line)
            if match:
                # Extract the second percentage
                percentage = match.group(2)

                # Extract the last string of the line which should be taken as the key for the dictionary
                function_identifier = match.group(3)

                # Remove any leading underscores and '@' symbols
                cleaned_function_identifier = re.sub(r'^[_@]+', '', function_identifier)
                #if the key already exists keep the highest of the two values
                if cleaned_function_identifier in results:
                    if results[cleaned_function_identifier] < percentage:
                        results[cleaned_function_identifier] = percentage
                else:
                    # Add to the dictionary
                    results[cleaned_function_identifier] = percentage
    
    return results



def extract_event_count(filename):
    # Regular expression to match the line
    pattern = r"Event count \(approx\.\): (\d+)"
    
    try:
        with open(filename, 'r') as file:
            for line in file:
                # Search for the pattern in each line
                match = re.search(pattern, line)
                if match:
                    # If a match is found, convert the captured group to an integer
                    return int(match.group(1))
    except FileNotFoundError:
        print("File not found.")
    except Exception as e:
        print(f"An error occurred: {e}")
    
    return None 




# Check if the number of arguments is exactly three (the script name and two filenames)
if len(sys.argv) == 3:
    file1 = sys.argv[1]
    file2 = sys.argv[2]
    #print(f"Filename 1: {file1}")
    #print(f"Filename 2: {file2}")
elif len(sys.argv) == 2 and sys.argv[1] =="error":
    print(f"Custom Metric: 999999999999")
    sys.stderr.write(f"Custom Metric: 999999999999")
else:
    print("Error: Two filenames must be provided.")
    sys.exit(1)  # Exit the script with an error code
# Usage example, you'll need to replace 'your_file_path.txt' with your actual file path
events= extract_event_count(file1)
result_dict = process_file(file1)


# Usage


section_weights = read_and_process_file(file2)
#print(section_weights)
#print(len(section_weights))

final_value=0
for key,value in section_weights.items():
    if key in result_dict:
        #print(f"section {key} which has been executed {result_dict[key]} percent of time has weight: {value}")
        final_value+=float(result_dict[key])*value

print(f"Custom Metric: {final_value*events/10000}")
sys.stderr.write(f"Custom Metric: {final_value*events/10000}")