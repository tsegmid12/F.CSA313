# Bonus B2 — Def/Use жагсаалт

Энэ жагсаалт нь `DataFlowTargets.java` файлын бодит мөрийн дугаарыг ашиглав. Assignment-ийн RHS болон нөхцөлд уншигдсан хувьсагч use болно; LHS нь зөвхөн definition болно.

| Мөр | Кодын үйлдэл | Definition (def) | c-use | p-use |
|---:|---|---|---|---|
| 31 | `bonus(int[] attendance, int threshold)` | `attendance@31`, `threshold@31` (параметрийн эхний def) | — | — |
| 32 | `attendance == null || threshold < 0` | — | — | `attendance@31`, `threshold@31` (T/F; short-circuit нөхцөл) |
| 33 | `throw ...` | — | — | — |
| 35 | `int present = 0` | `present@35` | — | — |
| 36 | `int streak = 0` | `streak@36` | — | — |
| 37 | `int best = 0` | `best@37` | — | — |
| 38 | `for (int a : attendance)` | `a@38` (давталт бүрт дараагийн элементийн def) | — | `attendance@31` (дараагийн элемент байгаа эсэх T/F) |
| 39 | `if (a > 0)` | — | — | `a@38` (T/F) |
| 40 | `present = present + 1` | `present@40` | `present@35/@40` (өмнөх утга) | — |
| 41 | `streak = streak + 1` | `streak@41` | `streak@36/@41/@46` (өмнөх утга) | — |
| 42 | `if (streak > best)` | — | — | `streak@41`, `best@37/@43` (T/F) |
| 43 | `best = streak` | `best@43` | `streak@41` | — |
| 46 | `streak = 0` (else) | `streak@46` | — | — |
| 49 | `int score = 0` | `score@49` | — | — |
| 50 | `if (present >= threshold)` | — | — | `present@35/@40`, `threshold@31` (T/F) |
| 51 | `score = 5` | `score@51` | — | — |
| 53 | `if (best >= 5)` | — | — | `best@37/@43` (T/F) |
| 54 | `score = score + 3` | `score@54` | `score@49/@51` | — |
| 56 | `return score` | — | `score@49/@51/@54` | — |

## Kill / redefinition

- `present@40` нь өмнөх `present@35` эсвэл `present@40`-ийг дараагийн давталтын assignment дээр дахин тодорхойлно.
- `streak@41` нь өмнөх `streak@36`, `streak@41`, эсвэл `streak@46`-ийг дараагийн эерэг өдөр дахин тодорхойлно.
- **Else-ийн `streak@46` нь өмнөх `streak@36` эсвэл `streak@41` definition-ийг шууд kill хийнэ.** Харин `streak@46` өөрөө дараагийн эерэг өдрийн line 41-ийн RHS use-д хүрч чадна.
- `best@43` нь `best@37` эсвэл өмнөх `best@43`-ийг kill хийнэ.
- `score@51` нь `score@49`-ийг kill хийнэ; `score@54` нь `score@49` эсвэл `score@51`-ийг kill хийнэ. `score@54`-ийн RHS use нь тухайн мөрийн өөрийн шинэ def-ээс өмнө уншигдах тул DU хос үүсгэнэ.

## Def-clear ба биелэшгүй замын тайлбар

- Foreach-ийн `attendance@31 -> loop@38` p-use нь массив хоосон эсэхээс хамаарч T/F хоёр ирмэгтэй.
- `streak@46 -> use@42` хос боломжгүй: line 42 хүрэхийн өмнө эерэг өдөр line 41 дээр reset утгыг уншаад streak-ийг дахин тодорхойлно. Иймээс def-clear биш.
- `streak@36 -> use@42` мөн боломжгүй: line 42 хүрэхийн өмнө line 41 streak-ийг дахин тодорхойлно.
- `best@37 -> use@42` T боломжтой (эхний эерэг өдөр streak=1 > best=0); F боломжгүй, учир нь эхний эерэг өдөр заавал T болж best шинэчлэгдэнэ. Эерэг өдөр байхгүй бол line 42 огт хүрэхгүй.
- `best@37 -> use@53` T боломжгүй: best анхны 0 хэвээр бол нөхцөл F; 5 буюу түүнээс дээш болгохын тулд line 43 дээр дахин def хийнэ.
- Хүчингүй оролтын line 32 дахь throw нь функцийг дуусгадаг тул параметрийн def-үүд loop/body-ийн use-д хүрэхгүй.
- `score@49 -> use@54` зөвхөн line 50 F үед боломжтой; `score@51 -> use@54` зөвхөн line 50 T үед боломжтой. Эдгээр нь mutually exclusive замууд.
