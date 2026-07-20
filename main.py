import math
from kivy.app import App
from kivy.uix.boxlayout import BoxLayout
from kivy.uix.button import Button
from kivy.uix.slider import Slider
from kivy.uix.label import Label
from kivy.uix.image import Image
from kivy.graphics.texture import Texture
from PIL import Image as PILImage, ImageDraw

class MRGApp(App):
    def build(self):
        self.title = "Manga Raster Gen"
        self.mode = "radial"
        
        main_layout = BoxLayout(orientation='vertical', padding=10, spacing=10)
        self.img_widget = Image(size_hint_y=0.6)
        main_layout.add_widget(self.img_widget)
        
        main_layout.add_widget(Label(text="Densite / Lignes", size_hint_y=0.05))
        self.slider_density = Slider(min=20, max=200, value=80, step=5, size_hint_y=0.05)
        self.slider_density.bind(value=self.update_preview)
        main_layout.add_widget(self.slider_density)
        
        main_layout.add_widget(Label(text="Epaisseur", size_hint_y=0.05))
        self.slider_thick = Slider(min=1, max=10, value=2, step=1, size_hint_y=0.05)
        self.slider_thick.bind(value=self.update_preview)
        main_layout.add_widget(self.slider_thick)
        
        btn_layout = BoxLayout(size_hint_y=0.1, spacing=5)
        btn_radial = Button(text="Vitesse Radiale")
        btn_radial.bind(on_press=lambda x: self.set_mode("radial"))
        btn_layout.add_widget(btn_radial)
        
        btn_dots = Button(text="Trame Points")
        btn_dots.bind(on_press=lambda x: self.set_mode("dots"))
        btn_layout.add_widget(btn_dots)
        main_layout.add_widget(btn_layout)
        
        btn_export = Button(text="Exporter PNG (1024x1024)", size_hint_y=0.1, background_color=(0, 0.8, 0.4, 1))
        btn_export.bind(on_press=self.export_hd)
        main_layout.add_widget(btn_export)
        
        self.update_preview()
        return main_layout

    def set_mode(self, mode_name):
        self.mode = mode_name
        self.update_preview()

    def generate_pil_image(self, width, height):
        density = int(self.slider_density.value)
        thick = int(self.slider_thick.value)
        img = PILImage.new("L", (width, height), 255)
        draw = ImageDraw.Draw(img)
        
        if self.mode == "radial":
            cx, cy = width // 2, height // 2
            max_r = math.hypot(cx, cy)
            for i in range(density):
                angle = (2 * math.pi / density) * i
                x1 = cx + math.cos(angle) * (max_r * 0.2)
                y1 = cy + math.sin(angle) * (max_r * 0.2)
                x2 = cx + math.cos(angle) * max_r
                y2 = cy + math.sin(angle) * max_r
                draw.line([(x1, y1), (x2, y2)], fill=0, width=thick)
        elif self.mode == "dots":
            spacing = max(8, 200 // (density // 5 + 1))
            for y in range(0, height, spacing):
                for x in range(0, width, spacing):
                    draw.ellipse([x, y, x + thick * 2, y + thick * 2], fill=0)
        return img

    def update_preview(self, *args):
        pil_img = self.generate_pil_image(400, 400).convert("RGBA")
        data = pil_img.tobytes()
        texture = Texture.create(size=(400, 400), colorfmt='rgba')
        texture.blit_buffer(data, colorfmt='rgba', bufferfmt='ubyte')
        texture.flip_vertical()
        self.img_widget.texture = texture

    def export_hd(self, instance):
        hd_img = self.generate_pil_image(1024, 1024)
        hd_img.save("manga_raster_output.png")

if __name__ == "__main__":
    MRGApp().run()
