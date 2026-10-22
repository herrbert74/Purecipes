package app.purecipes.shared.ui.icon.appliance

import androidx.compose.ui.graphics.vector.ImageVector
import app.purecipes.shared.domain.model.Appliance
import app.purecipes.shared.ui.icon.AppIcons

val Appliance.icon: ImageVector
	get() = when (this) {
		Appliance.AIR_FRYER -> AppIcons.AirFryer
		Appliance.BLENDER -> AppIcons.Blender
		Appliance.DEEP_FRYER -> AppIcons.DeepFryer
		Appliance.FOOD_PROCESSOR -> AppIcons.FoodProcessor
		Appliance.GRILL -> AppIcons.Grill
		Appliance.HAND_MIXER -> AppIcons.HandMixer
		Appliance.IMMERSION_BLENDER -> AppIcons.ImmersionBlender
		Appliance.JUICER -> AppIcons.Juicer
		Appliance.KETTLE -> AppIcons.Kettle
		Appliance.MICROWAVE -> AppIcons.Microwave
		Appliance.OVEN -> AppIcons.Oven
		Appliance.PASTA_MAKER -> AppIcons.PastaMaker
		Appliance.PRESSURE_COOKER -> AppIcons.PressureCooker
		Appliance.SANDWICH_MAKER -> AppIcons.SandwichMaker
		Appliance.SLOW_COOKER -> AppIcons.SlowCooker
		Appliance.SPICE_GRINDER -> AppIcons.SpiceGrinder
		Appliance.STAND_MIXER -> AppIcons.StandMixer
		Appliance.STEAMER -> AppIcons.Steamer
		Appliance.STOVETOP -> AppIcons.Stovetop
		Appliance.WAFFLE_MAKER -> AppIcons.WaffleMaker
	}
