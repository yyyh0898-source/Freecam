# LeviPack Freecam Android Engine (v1.26.52)
**Primary Asset Bundle Tag:** `LeviAssetBundle`

---

## မြန်မာဘာသာ (Burmese Instructions)
LeviPack Freecam အား Minecraft Bedrock 1.26.52 တွင် အသုံးပြုရန် လမ်းညွှန်ချက်:
1. ရရှိလာသော `.levipack` ဖိုင်ကို LeviLamina သို့မဟုတ် Bedrock Modloader ၏ `packs` ဖိုင်တွဲထဲသို့ ထည့်သွင်းပါ။
2. Android Studio ဖြင့် ပြင်ဆင်အသုံးပြုလိုပါက `aar/` ဖိုင်တွဲကို Android Library (AAR) အဖြစ် build ပြုလုပ်နိုင်ပါသည်။
3. အဓိက အင်္ဂါရပ်များ:
   - **60 FPS Hardware VSync**: Choreographer နည်းပညာဖြင့် ဖုန်းအားလုံးတွင် frame နှုန်းတည်ငြိမ်စေခြင်း။
   - **Smooth Interpolation**: Catmull-Rom spline နှင့် exponential inertia ဖြင့် ကင်မရာ ရွေ့လျားမှု ချောမွေ့ခြင်း။
   - **Customizable FOV**: အနီး/အဝေး zoom နှင့် cinematic transitions များ။
   - **Memory Footprint Optimizer**: DirectByteBuffer နှင့် `LeviAssetBundle` asset caching စနစ်ဖြင့် memory crash မဖြစ်အောင် ထိန်းညှိထားခြင်း။

---

## English Technical Architecture
- **Target Bedrock Version:** 1.26.52
- **Frame Sync:** Hardware Choreographer 60 FPS (60 Hz Target)
- **Asset Streaming Tag:** `LeviAssetBundle` (LRU Caching & Async Coroutine Pool)
- **Native NDK:** C++20 Double-buffered Lock-free Camera Matrix Flip
- **Smoothing Algorithm:** INERTIAL

Package generated with LeviPack Freecam Studio.
