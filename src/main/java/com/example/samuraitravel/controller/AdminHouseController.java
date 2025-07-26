package com.example.samuraitravel.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.samuraitravel.entity.House;
import com.example.samuraitravel.form.HouseEditForm;
import com.example.samuraitravel.form.HouseRegisterForm;
import com.example.samuraitravel.repository.HouseRepository;
import com.example.samuraitravel.service.HouseService;

@Controller
@RequestMapping("/admin/houses")	// 共通パスを自動付記
public class AdminHouseController {
// HouseRepositoryのオブジェクトを利用する（HouseRepositoryクラスを依存関係）
	private final HouseRepository houseRepository;	 //	初期化後にカプセル化（安全性向上のため）
	private final HouseService houseService;
	
// DI：依存しているクラスから [オブジェクト注入] して [フィールド初期化]
	// @Autowired ：DIコンテナに登録されたコンストラクタをDIする役割
	//				（対象のコンストラクタが「1つ」の場合は書かなくてもOK）
	public AdminHouseController(HouseRepository houseRepository, HouseService houseService) {
		this.houseRepository = houseRepository;
		this.houseService = houseService;
		
	}
	
	@GetMapping		// ※ @RequestMappingでルートパスの基準値を指定済のため ︎("/admin/houses")は省略
// ページネーションの設定
	// ▶ 明示的に表示条件を指定する場合 [@PageableDefault]（※ ︎Pageable型の引数を指定すると、デフォルト：ページ番号0、サイズ20件、並べ替え条件なしが 自動適用される）
	public String index(Model model, @PageableDefault(page = 0, size = 10, sort = "id", direction = Direction.ASC) Pageable pageable, @RequestParam(name = "keyword", required = false) String keyword) {
	// HouseRepositoryインターフェースのfindAll()メソッドですべての民宿データを取得して、戻り値[Page型]の変数 housePage に代入
	//  Page<House> housePage = houseRepository.findAll(pageable);	検索機能の実装時に削除
		Page<House> housePage;
		
		if (keyword != null && !keyword.isEmpty()) {
			housePage = houseRepository.findByNameLike("%" + keyword + "%", pageable);
		} else {
			housePage = houseRepository.findAll(pageable);
		}
		
	// Modelクラスを使ってビューにデータを渡す
		model.addAttribute("housePage", housePage);	// addAttribute(第1引数：View側から参照する変数名 / 第2引数：Viewに渡すデータ)
		model.addAttribute("keyword", keyword);

		return "admin/houses/index";	// Viewのパス
	}
	
	@GetMapping("/{id}")
	public String show(@PathVariable(name = "id") Integer id, Model model) {
		House house = houseRepository.getReferenceById(id);
		
		model.addAttribute("house", house);
		
		return "admin/houses/show";
	}
	
	@GetMapping("/register")
	public String register(Model model) {
		model.addAttribute("houseRegisterForm", new HouseRegisterForm());
		
		return "admin/houses/register";
	}
	
	@PostMapping("/create")
	public String create(@ModelAttribute @Validated HouseRegisterForm houseRegisterForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "admin/houses/register";
		}
		
		houseService.create(houseRegisterForm);
		redirectAttributes.addFlashAttribute("successMessage", "民宿を登録しました。");
		
		return "redirect:/admin/houses";
	}
	
	@GetMapping("/{id}/edit")
	public String edit(@PathVariable(name = "id") Integer id, Model model) {
		House house = houseRepository.getReferenceById(id);
		String imageName = house.getImageName();
		HouseEditForm houseEditForm = new HouseEditForm(house.getId(), house.getName(), null, house.getDescription(), house.getPrice(), house.getCapacity(), house.getPostalCode(), house.getAddress(), house.getPhoneNumber());
		
		model.addAttribute("imageName", imageName);
		model.addAttribute("houseEditForm", houseEditForm);
		
		return "admin/houses/edit";
	}
	
	@PostMapping("/{id}/update")
    public String update(@ModelAttribute @Validated HouseEditForm houseEditForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {        
        if (bindingResult.hasErrors()) {
            return "admin/houses/edit";
        }
        
        houseService.update(houseEditForm);
        redirectAttributes.addFlashAttribute("successMessage", "民宿情報を編集しました。");
        
        return "redirect:/admin/houses";
    }
	
    @PostMapping("/{id}/delete")
    public String delete(@PathVariable(name = "id") Integer id, RedirectAttributes redirectAttributes) {        
        houseRepository.deleteById(id);
                
        redirectAttributes.addFlashAttribute("successMessage", "民宿を削除しました。");
        
        return "redirect:/admin/houses";
    }  
}

// ページネーションの設定をしても、ブラウザにはデフォルト条件で表示される
	// public String index(Model model, Pageable pageable) {}
	// http://localhost:8080/admin/houses?page=1&size=10&sort=id,DESC ←　URLに直接page=1,size=10, sort=id, 昇順降順=DESCと設定すれば変更可能

// ポイント
	// 引数に@RequestParamアノテーションをつける
		// ▶ メソッドの引数に@RequestParamアノテーションをつけることで、フォームから送信されたパラメータ（リクエストパラメータ）をその引数にバインド（割当て）でき
			// name属性：取得するリクエストパラメータ名
			// required属性：そのリクエストパラメータが必須かどうか（デフォルトはtrue）
			// defaultValue属性：リクエストパラメータの値が指定されていない、または空の場合のデフォルト値
	
	// keywordパラメータが存在するかどうかで処理を分ける
		// 部分一致検索 => 検索ワード(keyword)の前後に "%" をつける
	
	// ビューにkeyword（文字列）を渡す
		// model.addAttribute("keyword", keyword);